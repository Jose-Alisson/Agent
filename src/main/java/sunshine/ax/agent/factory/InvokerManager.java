package com.sunshineax.agent.factory;

import com.sunshineax.agent.AgentInvoker;
import com.sunshineax.agent.enums.SequenceExecutor;
import com.sunshineax.agent.event.EventHandler;
import com.sunshineax.agent.data.InvokerRequest;
import com.sunshineax.agent.validator.ValidatorFactory;
import com.sunshineax.agent.validator.annotations.Validate;

import java.util.*;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

public class InvokerManager {

    private SequenceExecutor sequenceExecutor = SequenceExecutor.PARALLEL;

    private final List<EventHandler> handlers = new ArrayList<>();;
    private final List<Consumer<InvokerRequest<?>>> next = new ArrayList<>();
    private final List<Consumer<Throwable>> error = new ArrayList<>();

    private final ValidatorFactory  validatorFactory = new ValidatorFactory();

    public InvokerManager(ExecutorInvoker executorInvoker) {
        SubmissionPublisher<InvokerRequest<?>> publisher = new SubmissionPublisher<>(
                executorInvoker.getExecutor(),
                Flow.defaultBufferSize()
        );

        executorInvoker.setPublisher(publisher);

        Flow.Subscriber<InvokerRequest<?>> subscriber = new Flow.Subscriber<>() {

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(InvokerRequest<?> item) {
                Object payload = item.getPayload();

                if(payload != null && payload.getClass().isAnnotationPresent(Validate.class)){
                    try {
                        validatorFactory.validator(payload);
                    } catch (Exception ex) {
                        error.forEach(e -> e.accept(ex));
                        return;
                    }
                }

                execute(executorInvoker, item);
                next.forEach(consumer -> consumer.accept(item));
            }

            @Override
            public void onError(Throwable throwable) {
                error.forEach(consumer -> consumer.accept(throwable));
            }

            @Override
            public void onComplete() {}
        };

        publisher.subscribe(subscriber);
    }

    private void execute(ExecutorInvoker executorInvoker, InvokerRequest<?> data) {
        handlers.stream()
                .sorted(Comparator.comparingInt(EventHandler::getPriority))
                .forEach(handler -> {
                    try {
                        Future<?> task = executorInvoker.getExecutor().submit(() -> handler.invoker(data));
                        if(sequenceExecutor.equals(SequenceExecutor.STREAM)){
                            task.get();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void registry(AgentInvoker<?> invoker, int priority) {
        EventHandler eventHandler = new EventHandler(invoker);
        eventHandler.setPriority(priority);
        handlers.add(eventHandler);
    }

    public void sequenceExecutor(SequenceExecutor sequenceExecutor) {
        this.sequenceExecutor = sequenceExecutor;
    }

    public void subscribe(Consumer<InvokerRequest<?>> next, Consumer<Throwable> error) {
        this.next.add(next);
        this.error.add(error);
    }

    public void subscribe(Consumer<InvokerRequest<?>> next) {
        this.next.add(next);
    }

    public List<EventHandler> getHandlers() {
        return handlers;
    }

    @Override
    public String toString() {
        return sequenceExecutor + "x" + handlers + "x" + next + "x" + error + "x" + System.identityHashCode(InvokerManager.this);
    }
}
