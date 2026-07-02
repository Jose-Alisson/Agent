package sunshineax.agent.factory.manager.register;

import sunshineax.agent.factory.ManagerFactory;
import sunshineax.agent.factory.executor.ExecutorCommandInvoker;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.Manager;
import sunshineax.agent.factory.manager.RegistryManagerToFactory;
import sunshineax.agent.factory.manager.impl.CommandInvokerManager;

public class CommandManagerRegister implements RegistryManagerToFactory {

    public CommandManagerRegister() {}

    @Override
    public Manager<?, ?, ?> factory(ManagerFactory managerFactory, ExecutorInvoker executorInvoker) {
        return new CommandInvokerManager<>(new ExecutorCommandInvoker<>(executorInvoker));
    }
}
