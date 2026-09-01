package sunshineax.agent.factory.manager.impl;

import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.annotations.Inject;
import sunshineax.agent.provider.AgentProvider;
import sunshineax.agent.factory.manager.LoaderService;

import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AgentManager implements LoaderService {

    private Map<Class<?>, AgentProvider> authentications = new ConcurrentHashMap<>();

    @Inject
    private DependencyProvider dependencyProvider;

    @Override
    public void load(URLClassLoader classLoader) {
        ServiceLoader<AgentProvider> authenticateServiceLoader = ServiceLoader.load(AgentProvider.class);

        for (AgentProvider agentProvider : authenticateServiceLoader) {
            authentications.putIfAbsent(agentProvider.getClass(), agentProvider);
            dependencyProvider.inject(agentProvider);
            dependencyProvider.add(agentProvider);
        }
    }

    public AgentProvider get(Class<?> clazz) {
        AgentProvider agentProvider = authentications.get(clazz);

        if (agentProvider == null) {
            throw new RuntimeException("No authenticate for " + clazz.getSimpleName());
        }

        return agentProvider;
    }
}
