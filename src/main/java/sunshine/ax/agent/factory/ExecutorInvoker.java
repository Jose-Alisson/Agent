package com.sunshineax.agent.factory;

import com.sunshineax.agent.data.InvokerRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

public class ExecutorInvoker {

    private final ExecutorService executor;
    private SubmissionPublisher<InvokerRequest<?>> publisher;

    public ExecutorInvoker(int poolSize) {
        executor = Executors
                .newFixedThreadPool(poolSize);
    }

    public void emit(InvokerRequest<?> data) {
        if (publisher == null) return;
        publisher.offer(data, 100, TimeUnit.MILLISECONDS, ((subscriber, o) -> {
            return false;
        }));
    }

    public void setPublisher(SubmissionPublisher<InvokerRequest<?>> publisher) {
        this.publisher = publisher;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
