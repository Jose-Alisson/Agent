package sunshineax.agent.invoker;

import sunshineax.agent.data.RequestContext;

public interface AgentEventInvoker<T> extends AgentInvoker<T> {

    void invoker(RequestContext<T> data);
}
