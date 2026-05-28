package com.sunshineax.agent.event;

import com.sunshineax.agent.AgentInvoker;
import com.sunshineax.agent.data.InvokerRequest;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EventHandler implements Flow.Subscription {

    private final AgentInvoker<?> invoker;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private int priority;

    private Consumer<Object> onNext;
    private Consumer<Throwable> onError;
    private Runnable onComplete;

    public EventHandler(AgentInvoker<?> invoker) {
        this.invoker = invoker;
    }

    public EventHandler onNext(Consumer<Object> onNext) {
        this.onNext = onNext;
        return this;
    }

    public EventHandler onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    public EventHandler onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public void invoker(InvokerRequest<?> data) {
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

    public AgentInvoker<?> getInvoker() {
        return invoker;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
