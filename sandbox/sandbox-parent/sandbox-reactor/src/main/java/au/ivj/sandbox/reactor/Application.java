package au.ivj.sandbox.reactor;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.publisher.TopicProcessor;
import reactor.io.netty.ReactiveNet;
import reactor.io.netty.preprocessor.CodecPreprocessor;
import reactor.io.netty.tcp.TcpClient;
import reactor.io.netty.tcp.TcpServer;

import java.util.Arrays;

import static reactor.bus.selector.Selectors.$;

public class Application
{
    public static void main(String... args) throws InterruptedException
    {
        EventBus bus = EventBus.create(TopicProcessor.create(), 4);

        //Flux<String> fileSaver = Flux.cre

        bus.on($("events.fileReceived"), event -> {
            System.out.println("Received file: " + event.getData() + " -> " +
                    Thread.currentThread().getName());
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });

        TcpServer<String, String> server = ReactiveNet.tcpServer(s -> s.listen(56010).preprocessor(CodecPreprocessor
                .linefeed()));

        TcpClient<String, String> client = ReactiveNet.tcpClient(s -> s.connect("localhost", 56010).preprocessor
                (CodecPreprocessor.linefeed()));

        server.start(channel ->
                {
                    channel.input().useCapacity(10).publishOn(SchedulerGroup.io("ReceiverPool", 4, 5)).doOnNext
                            (data -> {
                                System.out.println("Reading " + data + " - " + Thread.currentThread().getName());
                                bus
                                    .notify
                                            ("events.fileReceived", Event.wrap(data + " " + Thread.currentThread()
                                                    .getName()));}).subscribe();
                    return Flux.never();
                }
        );

        client.start(ch -> {
                    Flux fluxTemp = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F", "G")).doOnNext(e -> {
                        try
                        {
                            Thread.sleep(1000);
                            System.out.println("Sent: " + e + " -> " + Thread.currentThread().getName());
                        }
                        catch (InterruptedException e1)
                        {
                            e1.printStackTrace();
                        }
                    }).dispatchOn
                            (SchedulerGroup.io("Sender", 4, 5));
                    Flux flux = Flux.merge(2, fluxTemp, fluxTemp, fluxTemp, fluxTemp);
                    return ch.writeWith(flux);
                }
        ).get();

        Thread.sleep(5000);
    }


}
