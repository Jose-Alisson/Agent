package sunshineax.agent.handler;

import sunshineax.agent.invoker.EventInvoker;
import sunshineax.agent.invoker.Invoker;
import sunshineax.agent.data.RequestContext;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EventHandler<T> implements Flow.Subscription {

    private final EventInvoker<T> invoker;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private int priority;

    private Consumer<Object> onNext;
    private Consumer<Throwable> onError;
    private Runnable onComplete;

    public EventHandler(EventInvoker<T> invoker) {
        this.invoker = invoker;
    }

    public EventHandler<T> onNext(Consumer<Object> onNext) {
        this.onNext = onNext;
        return this;
    }

    public EventHandler<T> onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    public EventHandler<T> onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public void invoker(RequestContext<T> data) {
        if (!active.get()) return;

        try {
            invoker.invoker(data);
        }  catch (Exception e) {
            if (onError != null) {
                onError.accept(e);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void request(long n) {}

    @Override
    public void cancel() {
        active.set(false);
        if (onComplete != null) {
            onComplete.run();
        }
    }

    public Consumer<Throwable> getOnError() {
        return onError;
    }

    public Consumer<Object> getOnNext() {
        return onNext;
    }

    public Runnable getOnComplete() {
        return onComplete;
    }

    public Boolean getActive() {
        return active.get();
    }

    public Invoker<T> getInvoker() {
        return invoker;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return invoker.toString();
    }
}
