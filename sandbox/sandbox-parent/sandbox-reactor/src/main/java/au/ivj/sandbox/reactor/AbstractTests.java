package au.ivj.sandbox.reactor;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.util.EmptySubscription;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generic helpers
 */
public abstract class AbstractTests
{
    protected static AtomicLong sequence = new AtomicLong(0);

    protected void sleepQuiet(long l) {
        try
        {
            Thread.sleep(l);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    protected Publisher<String> emit20valuesIn5Sec() {
        return s ->
            {
                s.onSubscribe(EmptySubscription.INSTANCE);
                for (int i = 0; i < 20; i++) {
                    sleepQuiet(250);
                    s.onNext("Value:" + i);
                }
                s.onComplete();
            };
    }

    protected Subscriber sysoutSubscriber(String name) {
        return new Subscriber()
        {
            @Override
            public void onSubscribe(Subscription s)
            {
                System.out.println("onSubscribe " + name);
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object o)
            {
                System.out.println("onNext " + name + ": " + o);
            }

            @Override
            public void onError(Throwable t)
            {
                System.out.println("onError " + name + ": " + t);
            }

            @Override
            public void onComplete()
            {
                System.out.println("onComplete " + name);
            }
        };
    }
}
