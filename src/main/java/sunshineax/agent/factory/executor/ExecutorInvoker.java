package sunshineax.agent.factory.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorInvoker {

    private final ExecutorService executor;

    public ExecutorInvoker(int poolSize) {
        executor = Executors
                .newFixedThreadPool(poolSize);
    }

    public ExecutorInvoker(ExecutorService executor) {
        this.executor = executor;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
