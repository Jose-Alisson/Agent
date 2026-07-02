package sunshineax.agent.factory;


import sunshineax.agent.exception.ConflictIntentResolutionException;
import sunshineax.agent.invoker.AgentEventInvoker;
import sunshineax.agent.invoker.AgentInvoker;
import sunshineax.agent.capability.Capability;
import sunshineax.agent.annotations.CapabilityHandler;
import sunshineax.agent.factory.manager.Manager;

import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryCapabilityFactory {

    private final Map<Capability, Manager<?, ?, ?>> managers = new ConcurrentHashMap<>();
    private final Map<String, Capability> capabilityMap = new ConcurrentHashMap<>();
    private final ManagerFactory managerFactory;

    public RegistryCapabilityFactory(ManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    @SuppressWarnings("unlocked")
    public void load(URLClassLoader classLoader) {
        ServiceLoader<AgentInvoker> loader = ServiceLoader.load(AgentInvoker.class, classLoader);

        for (AgentInvoker<?> discovery : loader) {
            Class<?> discoveryClass = discovery.getClass();
            Capability capability = getCapabilityFromAnnotation(discovery, discoveryClass);

            if(capabilityMap.containsKey(capability.intent())){
                Capability cap = capabilityMap.get(capability.intent());
                if(cap.typeRequest() != capability.typeRequest()){
                    throw new ConflictIntentResolutionException("Mais de uma intencao para tipos diferentes");
                }
            }else {
                capabilityMap.put(capability.intent(), capability);
            }

            managers.putIfAbsent(capability, managerFactory.create(get(discoveryClass)));
            Manager<AgentInvoker<?>, ?, ?> manager = getManager(capability);
            manager.registry(discovery);
        }
    }


    private Class<?> get(Class<?> clazz) {

        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            Class<?>[] interfaces = current.getInterfaces();
            for (Class<?> inter : interfaces) {
                if (AgentInvoker.class.isAssignableFrom(inter)) {
                    return inter;
                }
            }
            current = current.getSuperclass();
        }

        return null;
    }

    private Capability getCapabilityFromAnnotation(AgentInvoker<?> agentInvoker, Class<?> clazz) {
        CapabilityHandler ch = clazz.getAnnotation(CapabilityHandler.class);
        return new Capability(ch != null ? ch.intent() : clazz.getName(), agentInvoker.resolverType());
    }

    @SuppressWarnings("unchecked")
    public <E, T, R> Manager<E, T, R> getManager(Capability capability) {
        return (Manager<E, T, R>) managers.get(capability);
    }

    public Set<Capability> getCapabilities() {
        return managers.keySet();
    }

    public Capability getCapability(String intent) {
        return capabilityMap.get(intent);
    }
}
