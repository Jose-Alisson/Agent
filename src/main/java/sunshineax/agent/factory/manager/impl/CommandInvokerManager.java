package sunshineax.agent.factory.manager.impl;

import sunshineax.agent.invoker.AgentCommandInvoker;
import sunshineax.agent.data.RequestContext;
import sunshineax.agent.factory.executor.ExecutorCommandInvoker;
import sunshineax.agent.factory.manager.Manager;
import sunshineax.agent.validator.ValidatorFactory;

import java.util.concurrent.CompletableFuture;

public class CommandInvokerManager<T, R> implements Manager<AgentCommandInvoker<T, R>, T, R> {

    private final ExecutorCommandInvoker<T, R> executorCommandInvoker;

    private final ValidatorFactory validatorFactory = new ValidatorFactory();

    public CommandInvokerManager(ExecutorCommandInvoker<T, R> executorInvoker) {
        this.executorCommandInvoker = executorInvoker;
    }

    public CompletableFuture<R> executeAsync(RequestContext<T> request) {
        validatorFactory.validator(request.getPayload());
        return executorCommandInvoker.executeAsync(request);
    }

    @Override
    public R execute(RequestContext<T> requestContext) {
        validatorFactory.validator(requestContext.getPayload());
        return executorCommandInvoker.execute(requestContext);
    }

    @Override
    public void registry(AgentCommandInvoker<T, R> trAgentCommandInvoker) {
        executorCommandInvoker.setAgentCommandInvoker(trAgentCommandInvoker);
    }

    @Override
    public Class<?> resolverInvoker() {
        return AgentCommandInvoker.class;
    }
}
