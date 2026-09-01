package sunshineax.agent.factory.manager.impl;

import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.annotations.Inject;
import sunshineax.agent.factory.manager.LoaderService;
import sunshineax.agent.factory.resolver.RequestResolver;

import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RequestResolverManager implements LoaderService {

    private Map<Class<?>, RequestResolver<?>> requestResolvers = new ConcurrentHashMap<>();
    private Map<Class<?>, RequestResolver<?>> requestResolversByClass = new ConcurrentHashMap<>();

    @Inject
    private DependencyProvider dependencyProvider;

    public RequestResolverManager() {}

    public void load(URLClassLoader classLoader) {
        ServiceLoader<RequestResolver> loader = ServiceLoader.load(RequestResolver.class, classLoader);

        for (RequestResolver<?> requestResolver : loader) {
            dependencyProvider.inject(requestResolver);
            requestResolvers.put(requestResolver.resolveType(), requestResolver);
            requestResolversByClass.put(requestResolver.getClass(), requestResolver);
            dependencyProvider.add(requestResolver);
        }
    }

    private RequestResolverManager(Builder builder) {
        this.dependencyProvider = builder.dependencyProvider;
        this.requestResolvers.putAll(Arrays.stream(builder.requestResolvers).collect(
                Collectors.toMap(RequestResolver::resolveType, (r) -> r)));
    }

    public RequestResolver<?> resolver(Class<?> requestClass) {
        RequestResolver<?> requestResolver = requestResolvers.get(requestClass);
        if (requestResolver == null) {
            throw new IllegalStateException("No RequestResolver registered for " + requestClass.getName());
        }
        return requestResolver;
    }

    public RequestResolver<?> get(Class<?> requestClass) {
        RequestResolver<?> requestResolver = requestResolversByClass.get(requestClass);

        if (requestResolver == null) {
            throw new IllegalStateException("No RequestResolver registered for " + requestClass.getName());
        }
        return requestResolver;
    }

    @Override
    public String toString() {
        return "RequestResolverManager{" +
                "requestResolvers=" + requestResolvers +
                ", requestResolversByClass=" + requestResolversByClass +
                ", dependencyProvider=" + dependencyProvider +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RequestResolver<?>[] requestResolvers;
        private DependencyProvider dependencyProvider;

        public Builder() {
        }

        public Builder requestResolvers(RequestResolver<?>... requestResolvers) {
            this.requestResolvers = requestResolvers;
            return this;
        }

        public Builder dependencyProvider(DependencyProvider dependencyProvider) {
            this.dependencyProvider = dependencyProvider;
            return this;
        }

        public RequestResolverManager build() {
            return new RequestResolverManager(this);
        }
    }
}
