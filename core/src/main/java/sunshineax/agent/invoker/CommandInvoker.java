package sunshineax.agent.invoker;

import sunshineax.agent.data.RequestContext;

public interface CommandInvoker<T, R> extends Invoker<T> {

    R invoker(RequestContext<T> data);
}
