package au.ivj.sandbox.reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.subscriber.SignalEmitter;
import reactor.core.util.EmptySubscription;
import reactor.io.buffer.Buffer;
import reactor.io.netty.ReactiveNet;
import reactor.io.netty.preprocessor.CodecPreprocessor;
import reactor.io.netty.tcp.TcpClient;
import reactor.io.netty.tcp.TcpServer;

import java.util.concurrent.CountDownLatch;

/**
 * <p>Some tests with Reactor IO. To be honest, I don't think I can find a use for it :). The idea of "pulling" the data
 * is interesting, but we can easily do with standard Java APIs. The codecs and SLL are nice, but the Buffer
 * behaviour is quite funky.</p>
 * <p>I'm simulating files being sent from a computer to another and breaking in packages with a header and a
 * payload. The idea is that we could create "manual" multipart and restart from where we stopped as long as the
 * header has a unique identifier of the part. A GZIP or another could be put in place to compress the information
 * and SSL is easily pluggable to the server/client</p>
 */
public class ReactorIOTests
{
    /**
     * Quick test with the EmitterProcessor and signal emitter...
     */
    public static void emitterProcessorBasics(String... args) {
        EmitterProcessor<String>  processor = EmitterProcessor.replay(16, 2, true);
        processor.dispatchOn(SchedulerGroup.single("Emmiter", 16, true)).consume(e -> System.out.println("Received " +
                e + " " +
                Thread
                .currentThread()));
        SignalEmitter<String> emitter = processor.startEmitter();
        emitter.submit("A");
        emitter.submit("B");
        emitter.onComplete();
    }

    public static void main(String... args) throws Exception
    {
        TcpServer<byte[], byte[]> server =
                ReactiveNet.tcpServer(s -> s.listen(56010).preprocessor(CodecPreprocessor.byteArray()));

        TcpClient<byte[], byte[]> client = ReactiveNet.tcpClient(s -> s.connect("localhost", 56010).preprocessor
                (CodecPreprocessor.byteArray()));

        // Just to finish everything correctly instead of simply sleeping. 2 = client + server
        CountDownLatch partiesWorking = new CountDownLatch(2);

        server.start(channel ->
                {
                    // I'll only countdown after writing all the confirmations back to the client
                    CountDownLatch serverJobCount = new CountDownLatch(1);

                    // Using an emitter to write back to the client, just to show how it works
                    EmitterProcessor<byte[]> processor = EmitterProcessor.create();
                    final SignalEmitter<byte[]> writer = processor.startEmitter();
                    channel.writeWith(processor.publishOn(SchedulerGroup.single("Emitter", 16, true)).doAfterTerminate
                            (()
                            -> {
                        System.out.println("Server processor is finished.");
                        serverJobCount.countDown();
                        partiesWorking.countDown();
                    }))
                            .subscribe();

                    // Good to know...
                    writer.submit("I'm alive\n".getBytes());

                    // Just for tests, to know when the channel was closed. If it is closed prematurely (for
                    // instance, when the client finishes writing, we'd get an exception.
                    channel.on().close(() -> System.out.println("Closing"));

                    channel.input()
                            .doOnComplete(() -> System.out.println("Server finished"))
                            .subscribe(new Subscriber<byte[]>()
                            {
                                Buffer tempBuffer;

                                Subscription s;

                                @Override
                                public void onSubscribe(Subscription s)
                                {
                                    this.s = s;
                                    s.request(1l);
                                    System.out.println("Subscribed to server input");
                                }

                                @Override
                                public void onNext(byte[] bytes)
                                {
                                    /**
                                     * Looks more complicated than it really is... I was just too lazy to create a
                                     * "preprocessor", so I'm doing it by hand. I'm assuming that every message
                                     * starts with two integers indicating the size of the header and the size of the
                                     * body. Since the message may be broken by the limit of the channel, I simply
                                     * hold the incomplete message until the next flow, concatenate it and pretend it
                                     * came as a single mesage.
                                     */
                                    Buffer data = Buffer.wrap(bytes);
                                    if (tempBuffer != null)
                                    {
                                        data = new Buffer().append(tempBuffer, data).flip();
                                        tempBuffer = null;
                                    }
                                    while (data.remaining() > 0)
                                    {
                                        if (data.remaining() < 8)
                                        { // Unable to determine header and payload size
                                            byte[] remaining = new byte[data.remaining()];
                                            data.read(remaining);
                                            tempBuffer = Buffer.wrap(remaining);
                                            break;
                                        }

                                        int headerSize = data.readInt();
                                        int payloadSize = data.readInt();

                                        if (data.remaining() < headerSize + payloadSize)
                                        {
                                            byte[] remaining = new byte[data.remaining() + 8];
                                            data.position(data.position() - 8);
                                            data.read(remaining);
                                            tempBuffer = Buffer.wrap(remaining);
                                            break;
                                        }

                                        byte[] header = new byte[headerSize];
                                        data.read(header);

                                        String strHeader = new String(header);

                                        /*
                                         * If I receive this message from the client, it indicates that the flow is
                                         * over. I can't rely on the "complete" signal, since it generates no
                                         * information in this case. I may have buffered in the server a message that
                                          * I want to report back and I need to send it before the channel is closed.
                                           * If I used the flow itself to write to the channel and waited on the
                                           * "complete" signal, the channel would be closed before I had a chance to
                                           * report back to the client.
                                         */
                                        if (strHeader.equals("Thats it!")) {
                                            System.out.println("Wrapping it up...");
                                            writer.onComplete();
                                            break;
                                        }

                                        byte[] payload = new byte[payloadSize];
                                        data.read(payload);

                                        // Fancy processing of the date :D
                                        System.out.println("Header: " + strHeader);
                                        System.out.println("Payload: " + new String(payload));

                                        writer.submit(("Confirmed " + strHeader + "\n").getBytes());
                                    }

                                    // Gimme more!
                                    s.request(1l);
                                }

                                @Override
                                public void onError(Throwable t)
                                {
                                    System.out.println("Error reading data (server)");
                                }

                                @Override
                                public void onComplete()
                                {
                                    System.out.println("Server received completed signal");
                                }
                            });

                    return Mono.fromRunnable(() -> {
                        try
                        {
                            serverJobCount.await();
                            System.out.println("Shutting server down");
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }).publishOn(SchedulerGroup.single("Server lock", 1, true));
                }
        );

        client.startAndAwait(ch -> {
            CountDownLatch clientJobCount = new CountDownLatch(1);

            ch.input().subscribe(new Subscriber<byte[]>()
            {
                Subscription s;

                @Override
                public void onSubscribe(Subscription s)
                {
                    System.out.println("Client is prepared to read information sent by the server " + Thread
                            .currentThread());
                    s.request(1l);
                    this.s = s;
                }

                @Override
                public void onNext(byte[] bytes)
                {
                    System.out.println("Server replied: " + new String(bytes));
                    s.request(1);
                }

                @Override
                public void onError(Throwable t)
                {
                    System.out.println("Error reading data (client)");
                }

                @Override
                public void onComplete()
                {
                    System.out.println("Client finished receiving data from server and is shutting down");
                    clientJobCount.countDown();
                    partiesWorking.countDown();
                }
            });

            ch.writeWith(s -> {
                s.onSubscribe(EmptySubscription.INSTANCE);
                for (int i = 0; i < 100; i++)
                {
                    // Creating header + payload preceded by the length of each as int

                    String PAYLOAD = "THIS PAYLOAD COULD BE A BYTEBUFFER FROM A FILE";
                    String HEADER = "Hello World " + i + "!";

                    Buffer buffer = new Buffer()
                            .append(HEADER.length()) // Size of the header
                            .append(PAYLOAD.length()) // Size of the payload
                            .append(HEADER)
                            .append(PAYLOAD);

                    s.onNext(buffer.flip().asBytes()); // Send it!
                }

                s.onNext(new Buffer().append(9).append(0).append("Thats it!").flip().asBytes());

                s.onComplete();  // This will simply flush... Nothing is received at the server. It is OK for most
                // cases, but we want the server to send any backlog, so the "latch" is warranted
            }).get();

            return Mono.fromRunnable(() -> {
                try
                {
                    clientJobCount.await();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }).publishOn(SchedulerGroup.single("Client log", 1, true));
        });

        partiesWorking.await();

        System.out.println("And we are done!");
    }


}
