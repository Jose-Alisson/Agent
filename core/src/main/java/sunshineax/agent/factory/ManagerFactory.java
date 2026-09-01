package sunshineax.agent.factory;

import sunshineax.agent.annotations.Inject;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.LoaderService;
import sunshineax.agent.factory.manager.invoker.Manager;
import sunshineax.agent.factory.manager.register.RegistryManagerToFactory;

import java.net.URLClassLoader;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ManagerFactory implements LoaderService {

    private Map<Class<?>, Manager<?, ?, ?>> managers = new ConcurrentHashMap<>();
    private Map<Class<?>, RegistryManagerToFactory> registryManagerToFactory = new ConcurrentHashMap<>();

    @Inject
    private ExecutorInvoker executorInvoker;

    @Override
    public void load(URLClassLoader classLoader) {
        ServiceLoader<RegistryManagerToFactory> loaderManagers = ServiceLoader.load(RegistryManagerToFactory.class);

        for (RegistryManagerToFactory discovery : loaderManagers) {
            Manager<?, ?, ?> value = discovery.factory(this, executorInvoker);

            if (value != null) {
                managers.putIfAbsent(value.resolverInvoker(), value);
                registryManagerToFactory.putIfAbsent(value.resolverInvoker(), discovery);
            }
        }
    }

    public void registry(Manager<?, ?, ?> manager) {
        managers.put(manager.resolverInvoker(), manager);
    }

    public Manager<?,?,?> create(Class<?> type){
        RegistryManagerToFactory managerToFactory = registryManagerToFactory.get(type);

        if(managerToFactory == null){
            throw new NullPointerException("Cannot create managers for " + type);
        }

        return managerToFactory.factory(this, executorInvoker);
    }

    public ExecutorInvoker getExecutorInvoker() {
        return executorInvoker;
    }
}
