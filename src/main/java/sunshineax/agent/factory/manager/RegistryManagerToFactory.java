package sunshineax.agent.factory.manager;

import sunshineax.agent.factory.ManagerFactory;
import sunshineax.agent.factory.executor.ExecutorInvoker;

public interface RegistryManagerToFactory {

    Manager<?,?,?> factory(ManagerFactory managerFactory, ExecutorInvoker executorInvoker);

}
