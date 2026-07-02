package sunshineax.agent.factory.manager.register;

import sunshineax.agent.factory.ManagerFactory;
import sunshineax.agent.factory.executor.ExecutorEventInvoker;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.Manager;
import sunshineax.agent.factory.manager.RegistryManagerToFactory;
import sunshineax.agent.factory.manager.impl.EventInvokerManager;

public class EventManagerRegister implements RegistryManagerToFactory {

    public EventManagerRegister() {}

    @Override
    public Manager<?, ?, Void> factory(ManagerFactory managerFactory, ExecutorInvoker executorInvoker) {
        return new EventInvokerManager<>(new ExecutorEventInvoker<>(executorInvoker));
    }
}
