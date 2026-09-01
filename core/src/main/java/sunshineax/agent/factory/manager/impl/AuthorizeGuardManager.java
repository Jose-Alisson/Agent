package sunshineax.agent.factory.manager.impl;

import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.annotations.Inject;
import sunshineax.agent.annotations.Priority;
import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.session.SessionContext;
import sunshineax.agent.exception.UnauthorizedException;
import sunshineax.agent.factory.manager.LoaderService;
import sunshineax.agent.guard.AuthorizeGuard;

import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class AuthorizeGuardManager implements LoaderService {

    private Map<Class<?>, List<AuthorizeGuard<?>>> authorizeGuards = new ConcurrentHashMap<>();

    @Inject
    private DependencyProvider dependency;

    public AuthorizeGuardManager() {}

    @Override
    public void load(URLClassLoader urlClassLoader) {
        ServiceLoader<AuthorizeGuard> authorizeGuardServiceLoader = ServiceLoader.load(AuthorizeGuard.class, urlClassLoader);

        for (AuthorizeGuard<?> authorizeGuard : authorizeGuardServiceLoader) {
            List<AuthorizeGuard<?>> guards = authorizeGuards.put(authorizeGuard.resolveType(), List.of(authorizeGuard));
            if(guards != null) {
                guards.add(authorizeGuard);
            }
            dependency.inject(authorizeGuard);
        }
    }

    public boolean hasAuthorizeGuard(Class<?> authorizeGuardClass) {
        return authorizeGuards.containsKey(authorizeGuardClass);
    }

    public List<AuthorizeGuard<?>> getAuthorizeGuards(Class<?> clazz) {
        return authorizeGuards.get(clazz)
                .stream()
                .sorted(Comparator.comparingInt(this::getPriority)).toList();
    }

    private int getPriority(AuthorizeGuard<?> authorizeGuard) {
        Priority priority = authorizeGuard.getClass().getAnnotation(Priority.class);
        return priority != null ? priority.value() : 0;
    }

    public void authorizeChain(ExecutionContext executionContext, Object request){
        if (executionContext.isClient()) {
            if(executionContext.context().isEmpty()){
                throw new RuntimeException("Context is empty");
            }

            if (hasAuthorizeGuard(request.getClass())) {
                for (AuthorizeGuard<?> guard : getAuthorizeGuards(request.getClass())) {
                    guard.authorize(executionContext.context().get(), castType(request));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T castType(Object request) {
        return (T) request;
    }
}
