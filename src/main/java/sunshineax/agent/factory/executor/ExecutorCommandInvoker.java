package sunshineax.agent.factory.executor;

import sunshineax.agent.invoker.AgentCommandInvoker;
import sunshineax.agent.data.RequestContext;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ExecutorCommandInvoker<T, R> extends ExecutorInvoker {

    private AgentCommandInvoker<T, R> agentCommandInvoker;

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
        return CompletableFuture.supplyAsync(() -> agentCommandInvoker.invoker(request), getExecutor());
    }

    public void setAgentCommandInvoker(AgentCommandInvoker<T, R> agentCommandInvoker) {
        this.agentCommandInvoker = agentCommandInvoker;
    }
}
