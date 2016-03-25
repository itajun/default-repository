package au.ivj.sandbox.reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Base class for processor and BUS tests.
 */
public abstract class AbstractProcessorTests extends AbstractTests
{
    protected final int PARALLEL_CONSUMERS = 4;
    /**
     * Simple subscriber that stores data asynchronously.
     */
    protected static class DataStorer implements Subscriber<String>
    {
        private Repository repository;
        public DataStorer(Repository repository) {
            this.repository = repository;
        }
        @Override
        public void onSubscribe(Subscription s)
        {
            s.request(Long.MAX_VALUE);
        }
        @Override
        public void onNext(String s)
        {
            repository.addData(s);
        }
        @Override
        public void onError(Throwable t)
        {
            // Empty
        }
        @Override
        public void onComplete()
        {
            // Empty
        }
    }
    protected class Repository {
        public String name;
        private Map<Long, String> data = new HashMap<>();
        public Repository(String name) {
            this.name = name;
        }
        public long addData(String data) {
            long id = sequence.getAndIncrement();
            this.data.put(id, data);
            System.out.println("Storing [" + data + " | id:" + id + "] on repository [" + name + "] with thread [" + Thread
                    .currentThread() + "]");
            sleepQuiet(2000 + new Random().nextInt(3000));
            return id;
        }
        public String getData(long id) {
            sleepQuiet(500);
            String result = this.data.get(id);
            return result == null ? "" : result;
        }
    }
}
