package com.sunshineax.agent.factory;

import com.sunshineax.agent.AgentInvoker;
import com.sunshineax.agent.annotations.Priority;

import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryFactory {

    private final Map<Class<?>, InvokerManager> registryInvokers;
    private final ExecutorInvoker executorInvoker;

    public DiscoveryFactory(ExecutorInvoker executorInvoker) {
        this.executorInvoker = executorInvoker;
        registryInvokers = new ConcurrentHashMap<>();
    }

    public void load(URLClassLoader classLoader) {
        ServiceLoader<AgentInvoker> loader = ServiceLoader.load(AgentInvoker.class, classLoader);

        for (AgentInvoker<?> discovery : loader) {

           registryInvokers.computeIfAbsent(discovery.type(), k -> new InvokerManager(executorInvoker));

            if(registryInvokers.containsKey(discovery.type())){
                InvokerManager invokerManager = registryInvokers.get(discovery.type());

                boolean hasPriority = discovery.getClass().isAnnotationPresent(Priority.class);
                if(hasPriority){
                    Priority priority = discovery.getClass().getAnnotation(Priority.class);
                    invokerManager.registry(discovery, priority.value());
                } else {
                    invokerManager.registry(discovery, 0);
                }
            }
        }
    }

    public InvokerManager get(Class<?> type){
        return registryInvokers.get(type);
    }
}
