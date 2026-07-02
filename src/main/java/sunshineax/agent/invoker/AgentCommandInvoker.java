package sunshineax.agent.invoker;

import sunshineax.agent.data.RequestContext;

import java.util.concurrent.CompletableFuture;

public interface AgentCommandInvoker<T, R> extends AgentInvoker<T> {

    R invoker(RequestContext<T> data);
}
