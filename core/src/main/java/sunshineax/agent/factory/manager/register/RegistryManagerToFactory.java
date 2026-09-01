package sunshineax.agent.factory.manager.register;

import sunshineax.agent.factory.ManagerFactory;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.invoker.Manager;

public interface RegistryManagerToFactory {

    Manager<?,?,?> factory(ManagerFactory managerFactory, ExecutorInvoker executorInvoker);

}
