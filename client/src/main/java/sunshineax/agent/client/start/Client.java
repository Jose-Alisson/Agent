package sunshineax.agent.client.start;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.annotations.Capability;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.capability.SessionCapability;
import sunshineax.agent.client.broker.ClientBroker;
import sunshineax.agent.config.ClientConfig;
import sunshineax.agent.data.message.sub.Authenticate;
import sunshineax.agent.data.message.sub.Discovery;
import sunshineax.agent.data.message.sub.Response;
import sunshineax.agent.data.session.Session;
import sunshineax.agent.data.session.SessionContext;
import sunshineax.agent.emitter.Emitter;
import sunshineax.agent.enums.TypeIntent;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.LoaderServiceManager;
import sunshineax.agent.factory.manager.impl.RegistryCapabilityManager;
import sunshineax.agent.initialize.ClientChannelInitializer;
import sunshineax.agent.registry.impl.SessionRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Client {

    private final String host;
    private final int port;

    private final ClientChannelInitializer initializer;

    private final Emitter emitter;
    private final DependencyProvider provider;

    private List<Session> sessions;

    public List<Session> getSessionsA(){
        return sessions;
    }

    public Client(ClientConfig config) {
        this.emitter = config.getEmitter();
        this.host = config.getUri().getHost();
        this.port = config.getUri().getPort();

        provider = config.getDependency();
        provider.add(provider);
        provider.add(new ExecutorInvoker(8));
        provider.add(config.getRequestSerialize());
        provider.add(emitter);

        LoaderServiceManager loaderServiceManager = new LoaderServiceManager(config.getUrlClassLoader(), config.getDependency());
        loaderServiceManager.loader();

        Broker broker = provider.injectAndReturn(ClientBroker.builder().emitter(emitter).build());

        provider.add(broker);
        provider.inject(config.getRequestSerialize());
        provider.inject(emitter);

        this.initializer = new ClientChannelInitializer(broker, WebSocketClientHandshakerFactory.newHandshaker(
                config.getUri(),
                WebSocketVersion.V13,
                null,
                true,
                new DefaultHttpHeaders()
        ));
    }

    public void connect() {
        CompletableFuture.runAsync(() -> {
            tryCloseChannel(() -> {
                initializer.setOnConnect(() -> {
                    sessions = getSessions();
                    System.out.println(sessions.size());
                });
                initializer.connect(host, port);
            });
        });
    }

    public void connect(Runnable onConnect) {
        CompletableFuture.runAsync(() -> {
            tryCloseChannel(() -> {
                initializer.setOnConnect(() -> {
                    sessions = getSessions();
                    System.out.println(sessions.size());
                    onConnect.run();
                });
                initializer.connect(host, port);
            });
        });
    }

    public void connect(Runnable onConnect, Runnable onDisconnect) {
        CompletableFuture.runAsync(() -> {
            tryCloseChannel(() -> {
                initializer.setOnConnect(() -> {
                    sessions = getSessions();
                    System.out.println(sessions.size());
                    onConnect.run();
                });
                initializer.setOnDisconnect(onDisconnect);
                initializer.connect(host, port);
            });
        });
    }

    private void tryCloseChannel(TryCloseChannel run) {
        try {
            run.run();
        } catch (Exception e) {
            e.printStackTrace();
            close();
            throw new RuntimeException(e);
        }
    }

    public void close() {
        initializer.close();
    }

    public CompletableFuture<Response> publish(Object payload) {
        return emitter.publish(sessions, payload);
    }

    public CompletableFuture<Response> discovery() {
        return emitter.publish(sessions, Discovery
                .builder()
                .id(UUID.randomUUID().toString())
                .sessionCapabilities(getCapabilities())
                .build());
    }

    public CompletableFuture<Response> authenticate(String id, String secret) {
        return emitter.publish(sessions, Authenticate.builder()
                .id(UUID.randomUUID().toString())
                .agent(id)
                .secret(secret)
                .build());
    }

    private Set<SessionCapability> getCapabilities() {
        RegistryCapabilityManager capabilityManager = (RegistryCapabilityManager) provider.get(RegistryCapabilityManager.class).getFirst();
        Set<SessionCapability> sessionCapabilities = new HashSet<>();
        capabilityManager.getCapabilityClasses().forEach((key, value) -> {
            Capability capability = value.getAnnotation(Capability.class);
            sessionCapabilities.add(new SessionCapability(key.intent(), capability != null ? capability.type() : TypeIntent.EVENT));
        });
        return sessionCapabilities;
    }

    private List<Session> getSessions() {
        SessionRegistry sessions = (SessionRegistry) provider.get(SessionRegistry.class).getFirst();
        return sessions.getSessionsContext().values().stream().map(SessionContext::getSession).collect(Collectors.toList());
    }

    private interface TryCloseChannel {
        void run() throws Exception;
    }
}
