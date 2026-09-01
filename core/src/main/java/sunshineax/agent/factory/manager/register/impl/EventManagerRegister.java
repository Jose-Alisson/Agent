package sunshineax.agent.factory.manager.register.impl;

import sunshineax.agent.factory.ManagerFactory;
import sunshineax.agent.factory.executor.ExecutorEventInvoker;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.invoker.Manager;
import sunshineax.agent.factory.manager.invoker.impl.EventInvokerManager;
import sunshineax.agent.factory.manager.register.RegistryManagerToFactory;

public class EventManagerRegister implements RegistryManagerToFactory {

    public EventManagerRegister() {}

    @Override
    public Manager<?, ?, Void> factory(ManagerFactory managerFactory, ExecutorInvoker executorInvoker) {
        return new EventInvokerManager<>(new ExecutorEventInvoker<>(executorInvoker));
    }
}
