package sunshineax.agent.factory.manager.register.impl;

import sunshineax.agent.factory.ManagerFactory;
import sunshineax.agent.factory.executor.ExecutorCommandInvoker;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.invoker.Manager;
import sunshineax.agent.factory.manager.invoker.impl.CommandInvokerManager;
import sunshineax.agent.factory.manager.register.RegistryManagerToFactory;

public class CommandManagerRegister implements RegistryManagerToFactory {

    public CommandManagerRegister() {}

    @Override
    public Manager<?, ?, ?> factory(ManagerFactory managerFactory, ExecutorInvoker executorInvoker) {
        return new CommandInvokerManager<>(new ExecutorCommandInvoker<>(executorInvoker));
    }
}
