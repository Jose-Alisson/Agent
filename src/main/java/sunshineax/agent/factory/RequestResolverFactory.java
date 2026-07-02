package sunshineax.agent.factory;

import sunshineax.agent.factory.resolver.RequestResolver;
import sunshineax.agent.factory.resolver.impl.Dispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RequestResolverFactory {

    private final Map<Class<?>, RequestResolver<?>> requestResolvers ;

    public RequestResolverFactory(Set<RequestResolver<?>> requestResolvers) {
        this.requestResolvers = requestResolvers.stream().collect(Collectors.toMap(RequestResolver::resolveType, (r) -> r));
    }

    public RequestResolver<?> resolver(Class<?> requestClass){
        return requestResolvers.get(requestClass);
    }
}
