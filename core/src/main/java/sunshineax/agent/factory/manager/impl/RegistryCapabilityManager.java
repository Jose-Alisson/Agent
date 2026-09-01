package sunshineax.agent.factory.manager.impl;


import lombok.Getter;
import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.annotations.Inject;
import sunshineax.agent.capability.Capability;
import sunshineax.agent.exception.ConflictIntentResolutionException;
import sunshineax.agent.factory.ManagerFactory;
import sunshineax.agent.factory.manager.LoaderService;
import sunshineax.agent.factory.manager.invoker.Manager;
import sunshineax.agent.invoker.Invoker;

import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RegistryCapabilityManager implements LoaderService {

    @Getter
    private Map<Capability, Manager<?, ?, ?>> managers = new ConcurrentHashMap<>();

    @Getter
    private Map<String, Capability> capabilityMap = new ConcurrentHashMap<>();

    @Getter
    private Map<Capability, Class<?>> capabilityClasses = new ConcurrentHashMap<>();

    @Inject
    private ManagerFactory managerFactory;

    @Inject
    private DependencyProvider dependencyProvider;

    public RegistryCapabilityManager() {}

    @Override
    @SuppressWarnings("unlocked")
    public void load(URLClassLoader classLoader) {
        ServiceLoader<Invoker> loader = ServiceLoader.load(Invoker.class, classLoader);

        for (Invoker<?> discovery : loader) {
            Class<?> discoveryClass = discovery.getClass();
            sunshineax.agent.capability.Capability capability = getCapabilityFromAnnotation(discovery, discoveryClass);

            String intent = capability.intent().trim();

            if (capabilityMap.containsKey(intent)) {
                Capability cap = capabilityMap.get(intent);
                if (cap.typeRequest() != capability.typeRequest()) {
                    throw new ConflictIntentResolutionException("Mais de uma intencao para tipos diferentes");
                }
            } else {
                capabilityMap.put(intent, capability);
                capabilityClasses.put(capability, discoveryClass);
            }

            managers.putIfAbsent(capability, managerFactory.create(getInvokerClass(discoveryClass)));
            Manager<Invoker<?>, ?, ?> manager = getManager(capability);
            dependencyProvider.inject(discovery);
            manager.registry(discovery);
        }
    }


    private Class<?> getInvokerClass(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Class<?>[] interfaces = current.getInterfaces();
            for (Class<?> inter : interfaces) {
                if (Invoker.class.isAssignableFrom(inter)) {
                    return inter;
                }
            }
            current = current.getSuperclass();
        }

        return null;
    }

    private Capability getCapabilityFromAnnotation(Invoker<?> invoker, Class<?> clazz) {
        sunshineax.agent.annotations.Capability ch = clazz.getAnnotation(sunshineax.agent.annotations.Capability.class);
        return new Capability(ch != null ? ch.intent() : clazz.getName(), invoker.resolverType());
    }

    @SuppressWarnings("unchecked")
    public <E, T, R> Manager<E, T, R> getManager(sunshineax.agent.capability.Capability capability) {
        return (Manager<E, T, R>) managers.get(capability);
    }

    public Set<Capability> getCapabilities() {
        return new HashSet<>(capabilityMap.values());
    }

    public Capability getCapability(String intent) {
        return capabilityMap.get(intent);
    }

    @Override
    public String toString() {
        return capabilityMap.toString();
    }
}
