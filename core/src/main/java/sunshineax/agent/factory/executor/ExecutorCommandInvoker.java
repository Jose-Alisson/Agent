package sunshineax.agent.factory.executor;

import sunshineax.agent.invoker.CommandInvoker;
import sunshineax.agent.data.RequestContext;

import java.util.concurrent.CompletableFuture;

public class ExecutorCommandInvoker<T, R> extends ExecutorInvoker {

    private CommandInvoker<T, R> commandInvoker;

    public ExecutorCommandInvoker(int poolSize) {
        super(poolSize);
    }

    public ExecutorCommandInvoker(ExecutorInvoker executorInvoker) {
        super(executorInvoker.getExecutor());
    }

    public R execute(RequestContext<T> request) {
        return executeAsync(request).join();
    }

    public CompletableFuture<R> executeAsync(RequestContext<T> request) {
        return CompletableFuture.supplyAsync(() -> commandInvoker.invoker(request), getExecutor());
    }

    public void setAgentCommandInvoker(CommandInvoker<T, R> commandInvoker) {
        this.commandInvoker = commandInvoker;
    }
}
