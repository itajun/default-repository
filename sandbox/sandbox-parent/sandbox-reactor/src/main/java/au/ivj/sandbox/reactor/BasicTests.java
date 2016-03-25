package au.ivj.sandbox.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.timer.Timer;
import reactor.core.util.Exceptions;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Tests for Mono and Flux.
 */
public class BasicTests extends AbstractTests
{
    /**
     * A Mono is "Observable" that will be evaluated when a finalizer method is called (like get, after, consume...).
     */
    public void testMono() {
        // Simplest form
        Mono<String> just = Mono.just("A");
        System.out.println(just.get()); // A

        // If the optional isn't available, then it is "complete"
        Mono.justOrEmpty(Optional.of("A")).subscribe(sysoutSubscriber("Sub")); // onSubscribe, onNext A, onComplete
        Mono.justOrEmpty(Optional.empty()).subscribe(sysoutSubscriber("Sub")); // onSubscribe, onComplete

        // It is also easy to handle errors
        Mono.fromCallable(() -> { throw new IllegalArgumentException("Ops..."); }).subscribe(sysoutSubscriber("Sub")); // onError java.lang.IllegalArgumentException: Ops...

        // If you just want to evaluate and do something...
        Mono.fromCallable(() -> "A").consume(e -> System.out.println("Received: " + e));

        // Just wait for 1/2 second and timeout
        try
        {
            Mono.never().get(500); // Time is up!
        } catch (Exceptions.CancelException e) {
            System.out.println("Time is up!");
        }

        // Lets make it interesting using other threads...
        SchedulerGroup asyncGroup = SchedulerGroup.async("MyAsyncGroup");

        // And other interesting features... Like take the result from the first to reply:
        Mono monoA = Mono.fromCallable(() -> {
            sleepQuiet(2000); return "A" + Thread.currentThread();}).publishOn(asyncGroup);
        Mono monoB = Mono.fromCallable(() -> {
            sleepQuiet(1000); return "B" + Thread.currentThread();}).publishOn(asyncGroup);
        System.out.println("First to answer: " + Mono.any(monoA, monoB).get()); // B if you have more than 1 core, A otherwise ;)

        asyncGroup.shutdown();
    }

    /**
     * A Flux is also an observable and the operations applied to a Mono are also applicable to a Flux, but it is a
     * [possibly unbounded] sequence.
     */
    public void testFlux() {
        // Simplest form
        Flux<String> just = Flux.just("A", "B", "C"); // onSubscribe, OnNext A[...], OnNext C, OnComplete
        just.subscribe(sysoutSubscriber("Sub"));

        // We can concatenate...
        Flux concat = Flux.just("D", "E").concatWith(just);
        concat.subscribe(sysoutSubscriber("Sub")); // D, E, A...

        // Lets make it interesting using other threads...
        SchedulerGroup asyncGroup = SchedulerGroup.async("MyAsyncGroup");

        // Each subscriber in a different thread... Both will read the whole flux in parallel
        concat = concat.publishOn(asyncGroup);
        concat.doOnNext(e -> System.out.println("Sending " + e + " to sub1 on " + Thread.currentThread())).subscribe
                (sysoutSubscriber("SubA"));
        concat.doOnNext(e -> System.out.println("Sending " + e + " to sub2 on " + Thread.currentThread())).subscribe
                (sysoutSubscriber("SubB"));

        sleepQuiet(2000);

        // What if we subscribe a bit too late (items were already emitted)? It'll depend on the cache value... On
        // the example below, SubY will receive only the last 4 values before it was subscribed
        Flux my20 = Flux.from(emit20valuesIn5Sec()).publishOn(asyncGroup).cache(4);
        my20.subscribe(sysoutSubscriber("SubX"));
        sleepQuiet(2000);
        my20.subscribe(sysoutSubscriber("SubY"));

        // There is a best way to do this with a WorkQueueProcessor, but it is still fun...
        // We'll create a Mono from each item and publish into our execution group. They'll be executed in parallel
        // to the maximum size of the pool (default is max between the number of cores or 4). However the conversion
        // will create a new "Runnable" for each item... We can create a pool of runnables and only assign the values
        // using a WorkQueueProcessor
        Flux<String> fileList = Flux.fromStream(Arrays.asList("A", "B", "C").stream());
        Consumer<String> longConsumer = e -> {
            System.out.println("I'll write 5gb just from this value " + e + " using thread " + Thread.currentThread());
            sleepQuiet(10_000);
            System.out.println("Finished writing " + e);
        };
        fileList.flatMap(e -> Mono.fromRunnable(() -> longConsumer.accept(e)).publishOn(asyncGroup)).subscribe();

        // Timer is lazily initialized
        Timer.global();
        // And last but not least, we have all stream operator and a few more...
        Flux.from(emit20valuesIn5Sec())
                .skip(Duration.ofSeconds(1)) // ignore everything for 1 second
                .skip(5) // Then ignore 5 items
                .filter(e -> !e.contains("5")) // Then remove items with 5
                .map(e -> e.replace("12", "13")) // Then rename 12 to 13
                .distinct() // Now remove the duplicate
                //...
                .subscribe(sysoutSubscriber("Sub")); // Usually 10, 11, 13, 14...

        asyncGroup.shutdown();
    }

    public static void main(String... args) {
        BasicTests test = new BasicTests();
        test.testMono();
        test.testFlux();
    }
}
