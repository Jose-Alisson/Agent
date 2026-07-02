package sunshineax.agent.factory.manager;

import sunshineax.agent.data.RequestContext;

public interface Manager<E, T, R> {

    R execute(RequestContext<T> requestContext);

    void registry(E invoker);

    Class<?> resolverInvoker();
}
