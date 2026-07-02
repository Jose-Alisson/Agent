package sunshineax.agent.factory;

import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.Manager;
import sunshineax.agent.factory.manager.RegistryManagerToFactory;
import sunshineax.agent.invoker.AgentInvoker;

import java.net.URLClassLoader;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ManagerFactory {

    private final Map<Class<?>, Manager<?, ?, ?>> managers = new ConcurrentHashMap<>();

    private final Map<Class<?>, RegistryManagerToFactory> registryManagerToFactory = new ConcurrentHashMap<>();
    private final ExecutorInvoker executorInvoker;

    public ManagerFactory(ExecutorInvoker executorInvoker) {
        this.executorInvoker = executorInvoker;
    }

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

    public Manager<?, ?, ?> getManagerByResolverType(Class<?> invokerClass) {
        Class<?> atual = invokerClass;

        while (atual != null && atual != Object.class) {
            Manager<?, ?, ?> manager = managers.get(atual);

            if (manager != null) {
                return manager;
            }

            for (Class<?> iface : atual.getInterfaces()) {
                manager = managers.get(iface);

                if (manager != null) {
                    return manager;
                }
            }

            atual = atual.getSuperclass();
        }

        return null;
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
