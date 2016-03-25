package au.ivj.sandbox.reactor;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.SchedulerGroup;
import reactor.core.publisher.WorkQueueProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static reactor.bus.selector.Selectors.$;

/**
 * <p>Mostly tests with multi-thread and parallel execution. The proposed example is:</p>
 * <p>I'll receive a huge load of items, let's say they are file paths. I then have to process and store them in a
 * repository, which takes from 2 to 5 seconds per file.</p>
 * <p>To better use the resources available in this fictitious scenario, we'll create 4 different repositories
 * (think of them as folders or different HDs), and we'll distribute the processing of the received files as per the
 * availability of each repository (whether or not it is busy doing some work), but each file will be assigned a unique
 * id.
 * .</p>
 * <p>Since we haven't kept a map of repository per file, we'll have to implement a fork-join logic to find the file
 * content based on an id. We'll do so by querying all repositories in order to get the response. We'll do this using
 * 2 different algorithms (both return as soon as the result is found):
 * <ul>
 *     <li>Sequentially and all</li>
 *     <li>Parallel and all</li>
 * </ul>
 * <p>EventBus: The EB tests were included here simply because I'm too lazy to create another test :). Once the
 * processor is setup and some data is "created", we attach a BUS to interface with the processor. A real use case
 * would be a screen where the user can perform sync and async commands and be notified about events happening in
 * the processor, while keeping the interface processing completelly detached from the actual processing.</p>
 * </p>
 */
public class ProcessorTests extends AbstractProcessorTests
{
    protected final int PARALLEL_CONSUMERS = 4;

    /**
     *
     */
    protected void storeTests() {
        WorkQueueProcessor storerProcessor = WorkQueueProcessor.create(Executors.newFixedThreadPool
                (PARALLEL_CONSUMERS), 16, true);
        List<Repository> repositories = new ArrayList<>();

        for (int i = 0; i < PARALLEL_CONSUMERS; i++) {
            Repository repository = new Repository("Rep" + i);
            repositories.add(repository);
            storerProcessor.subscribe(new DataStorer(repository));
        }

        // Alternative implementation
        /*for (int i = 0; i < 10; i++) {
            storerProcessor.onNext("Value:" + i);
        }*/
        emit20valuesIn5Sec().subscribe(storerProcessor);

        sleepQuiet(10_000);

        System.out.println("Searching for id 4 sequentially");
        System.out.println("Found sequential: " + findAllWithScheduler(repositories, SchedulerGroup.single(), 4l));

        System.out.println("Searching for id 2 in parallel");
        System.out.println("Found parallel: " + findAllWithScheduler(repositories, SchedulerGroup.async(), 2l));

        sleepQuiet(3000);

        busTests(storerProcessor, repositories);

        storerProcessor.shutdown();
    }

    protected void busTests(WorkQueueProcessor storerProcessor, List<Repository> repositories) {
        WorkQueueProcessor<Event<?>> busProcessor = WorkQueueProcessor.create();
        EventBus bus = EventBus.create(busProcessor, 4);

        bus.on($("event.system.on.dataFound"), e -> System.out.println("Received async response of search: " + e.getData
                ()));
        bus.receive($("event.system.do.dataSearch"), e -> Event.wrap(findAllWithScheduler(repositories,
                SchedulerGroup.async(), (Long) e.getData())));
        bus.on($("event.system.do.manuallyEnterData"), e -> storerProcessor.onNext(e.getData()));

        System.out.println("Entering manual data XPTO");
        bus.notify("event.system.do.manuallyEnterData", Event.wrap("XPTO"));
        System.out.println("Requesting search for id 4 async");
        bus.notify("event.system.do.dataSearch", Event.wrap(4l).setReplyTo("event.system.on.dataFound"));
        System.out.println("Synchronously searching for id 5");
        bus.sendAndReceive("event.system.do.dataSearch", Event.wrap(5l), e -> System.out.println("Synchronously " +
                "found:" +
                " " + e.getData()));

        busProcessor.shutdown();
    }

    /**
     * Simply creates a thread tha looks for the value in the repository and appends it to the result if found. The
     * trick is on the scheduler used. If single, will do it sequentially, if async will do in parallel.
     */
    protected static String findAllWithScheduler(List<Repository> repositories, SchedulerGroup schedulerGroup, long
            value) {
        CountDownLatch threads = new CountDownLatch(repositories.size());
        CountDownLatch resultsFound = new CountDownLatch(1);
        StringBuilder result = new StringBuilder();
        for (Repository repository : repositories) {
            schedulerGroup.accept(() -> {
                System.out.println("Trying to find on repo [" + repository.name + "] with thread " + Thread
                        .currentThread() + "]");
                String data = repository.getData(value);
                if (data.length() > 0)
                {
                    resultsFound.countDown();
                    result.append(data);
                }
                threads.countDown();
            });
        }
        try
        {
            resultsFound.await();
        }
        catch (InterruptedException e)
        {
            return "ERROR";
        }
        finally
        {
            schedulerGroup.awaitAndShutdown(5, TimeUnit.SECONDS);
        }

        return result.toString();
    }

    public static void main(String... args) {
        ProcessorTests test = new ProcessorTests();
        test.storeTests();
    }
}
