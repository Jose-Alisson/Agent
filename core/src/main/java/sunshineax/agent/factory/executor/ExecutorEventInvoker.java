package sunshineax.agent.factory.executor;

import sunshineax.agent.data.RequestContext;

import java.util.concurrent.SubmissionPublisher;

public class ExecutorEventInvoker<T> extends ExecutorInvoker {

    private SubmissionPublisher<RequestContext<T>> publisher;

    public ExecutorEventInvoker(int poolSize) {
        super(poolSize);
    }

    public ExecutorEventInvoker(ExecutorInvoker executorEventInvoker) {
        super(executorEventInvoker.getExecutor());
    }

    public void publish(RequestContext<T> data) {
        if (publisher == null) return;
        publisher.submit(data);
    }

    public void setPublisher(SubmissionPublisher<RequestContext<T>> publisher) {
        this.publisher = publisher;
    }
}
