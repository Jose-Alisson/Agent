package sunshineax.agent.factory.manager;

import sunshineax.agent.acessor.DependencyProvider;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class LoaderServiceManager {

    private URLClassLoader classLoader;
    private DependencyProvider dependencyProvider;

    private Map<Class<?>, LoaderService> services = new ConcurrentHashMap<>();

    public LoaderServiceManager(URLClassLoader classLoader, DependencyProvider dependencyProvider) {
        this.classLoader = classLoader;
        this.dependencyProvider = dependencyProvider;
    }

    public void loader() {
        ServiceLoader<LoaderService> serviceLoader = ServiceLoader.load(LoaderService.class, classLoader);

        for (LoaderService service : serviceLoader) {
            services.put(service.getClass(), service);
            dependencyProvider.add(service);
            dependencyProvider.inject(service);
            service.load(classLoader);
        }
    }

    public LoaderService get(Class<?> clazz) {
        LoaderService loaderService = services.get(clazz);

        if (loaderService == null) {
            throw new IllegalStateException("No LoaderService registered for " + clazz);
        }

        return loaderService;
    }
}
