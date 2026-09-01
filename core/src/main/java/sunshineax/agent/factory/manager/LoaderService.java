package sunshineax.agent.factory.manager;

import sunshineax.agent.acessor.DependencyProvider;

import java.net.URLClassLoader;

public interface LoaderService {
    void load(URLClassLoader classLoader);
}
