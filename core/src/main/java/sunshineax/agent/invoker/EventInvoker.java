package sunshineax.agent.invoker;

import sunshineax.agent.data.RequestContext;

public interface EventInvoker<T> extends Invoker<T> {

    void invoker(RequestContext<T> data);
}
