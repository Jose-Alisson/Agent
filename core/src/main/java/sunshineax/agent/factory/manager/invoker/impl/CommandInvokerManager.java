package sunshineax.agent.factory.manager.invoker.impl;

import sunshineax.agent.invoker.CommandInvoker;
import sunshineax.agent.data.RequestContext;
import sunshineax.agent.factory.executor.ExecutorCommandInvoker;
import sunshineax.agent.factory.manager.invoker.Manager;
import sunshineax.agent.validator.ValidatorFactory;

import java.util.concurrent.CompletableFuture;

public class CommandInvokerManager<T, R> implements Manager<CommandInvoker<T, R>, T, R> {

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
    public void registry(CommandInvoker<T, R> trCommandInvoker) {
        executorCommandInvoker.setAgentCommandInvoker(trCommandInvoker);
    }

    @Override
    public Class<?> resolverInvoker() {
        return CommandInvoker.class;
    }
}
