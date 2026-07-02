package sunshineax.agent.x;

import sunshineax.agent.broker.ProxyBroker;
import sunshineax.agent.capability.SessionCapability;
import sunshineax.agent.data.request.Discovery;
import sunshineax.agent.data.request.Message;
import sunshineax.agent.enums.ContentType;
import sunshineax.agent.enums.TypeIntent;
import sunshineax.agent.start.Agent;
import sunshineax.agent.factory.ManagerFactory;
import sunshineax.agent.factory.RegistryCapabilityFactory;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.serialize.request.JacksonRequestSerialize;
import sunshineax.agent.start.Server;
import sunshineax.agent.x.request.Data;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

public class Launcher {

    URLClassLoader classLoader = new URLClassLoader(new URL[]{}, Launcher.class.getClassLoader());
    ExecutorInvoker executorInvoker = new ExecutorInvoker(24);
    JacksonRequestSerialize serialize = new JacksonRequestSerialize();
    ManagerFactory managerFactory = new ManagerFactory(executorInvoker);
    RegistryCapabilityFactory factory = new RegistryCapabilityFactory(managerFactory);

    ProxyBroker proxyBroker = new ProxyBroker(
            factory, serialize
    );

    public Launcher() {
        managerFactory.load(classLoader);
        factory.load(classLoader);
    }

    public ProxyBroker getServerBroker() {
        return proxyBroker;
    }

    public JacksonRequestSerialize getSerialize() {
        return serialize;
    }

    public RegistryCapabilityFactory getFactory() {
        return factory;
    }

    static void main() throws InterruptedException, URISyntaxException {
        Launcher launcher = new Launcher();

        Server server = new Server(8080, launcher.getServerBroker());
        server.start();

//        Agent client = Agent.builder()
//                .uri(new URI("ws://localhost:8080/agent"))
//                .capabilityFactory(launcher.getFactory())
//                .requestSerialize(launcher.getSerialize())
//                .build();
    }
}
