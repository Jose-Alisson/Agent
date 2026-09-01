package sunshineax.agent.factory.manager.invoker.impl;

import sunshineax.agent.invoker.EventInvoker;
import sunshineax.agent.annotations.Priority;
import sunshineax.agent.enums.SequenceExecutor;
import sunshineax.agent.factory.executor.ExecutorEventInvoker;
import sunshineax.agent.factory.manager.invoker.Manager;
import sunshineax.agent.handler.EventHandler;
import sunshineax.agent.data.RequestContext;
import sunshineax.agent.validator.ValidatorFactory;
import sunshineax.agent.validator.annotations.Validate;

import java.util.*;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

public class EventInvokerManager<T> implements Manager<EventInvoker<T>, T, Void> {

    private SequenceExecutor sequenceExecutor = SequenceExecutor.PARALLEL;

    private final List<EventHandler<T>> handlers = new ArrayList<>();
    private final List<Consumer<RequestContext<T>>> next = new ArrayList<>();
    private final List<Consumer<Throwable>> error = new ArrayList<>();

    private final ValidatorFactory validatorFactory = new ValidatorFactory();

    private final ExecutorEventInvoker<T> executorEventInvoker;

    public EventInvokerManager(ExecutorEventInvoker<T> executorInvoker) {
        this.executorEventInvoker = executorInvoker;

        SubmissionPublisher<RequestContext<T>> publisher = new SubmissionPublisher<>(
                executorInvoker.getExecutor(),
                Flow.defaultBufferSize()
        );

        executorInvoker.setPublisher(publisher);

        Flow.Subscriber<RequestContext<T>> subscriber = new Flow.Subscriber<>() {

            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(RequestContext<T> item) {
                handlers.stream()
                        .sorted(Comparator.comparingInt(EventHandler::getPriority))
                        .forEach(handler -> {
                            try {
                                Future<?> task = executorInvoker.getExecutor().submit(() -> handler.invoker(item));
                                if (sequenceExecutor.equals(SequenceExecutor.STREAM)) {
                                    task.get();
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                next.forEach(consumer -> consumer.accept(item));
            }

            @Override
            public void onError(Throwable throwable) {
                error.forEach(consumer -> consumer.accept(throwable));
                subscription.cancel();
            }

            @Override
            public void onComplete() {}
        };

        publisher.subscribe(subscriber);
    }

    @Override
    public Void execute(RequestContext<T> requestContext) {
        validate(requestContext);
        executorEventInvoker.publish(requestContext);
        return null;
    }

    private void validate(RequestContext<T> item) {
        Object payload = item.getPayload();
        if (payload != null && payload.getClass().isAnnotationPresent(Validate.class)) {
            try {
                validatorFactory.validator(payload);
            } catch (Exception ex) {
                error.forEach(e -> e.accept(ex));
                throw ex;
            }
        }
    }

    public void registry(EventInvoker<T> invoker) {
        EventHandler<T> eventHandler = new EventHandler<T>(invoker);
        eventHandler.setPriority(getPriority(invoker));
        handlers.add(eventHandler);
    }

    @Override
    public Class<?> resolverInvoker() {
        return EventInvoker.class;
    }

    private int getPriority(EventInvoker<?> invoker) {
        Priority priority = invoker.getClass().getAnnotation(Priority.class);
        return priority != null ? priority.value() : 0;
    }

    public void sequenceExecutor(SequenceExecutor sequenceExecutor) {
        this.sequenceExecutor = sequenceExecutor;
    }

    public void subscribe(Consumer<RequestContext<T>> next, Consumer<Throwable> error) {
        this.next.add(next);
        this.error.add(error);
    }

    public void subscribe(Consumer<RequestContext<T>> next) {
        this.next.add(next);
    }
}
