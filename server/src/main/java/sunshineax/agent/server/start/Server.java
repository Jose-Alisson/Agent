package sunshineax.agent.server.start;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.config.ServerConfig;
import sunshineax.agent.data.message.sub.Response;
import sunshineax.agent.data.session.Session;
import sunshineax.agent.emitter.Emitter;
import sunshineax.agent.factory.executor.ExecutorInvoker;
import sunshineax.agent.factory.manager.LoaderServiceManager;
import sunshineax.agent.initialize.ServerChannelInitializer;
import sunshineax.agent.registry.impl.RoomManager;
import sunshineax.agent.registry.impl.SessionRegistry;
import sunshineax.agent.server.broker.AxBroker;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Server {

    private final int port;
    private final Broker broker;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ServerBootstrap b = new ServerBootstrap();

    private final Emitter emitter;

    private final DependencyProvider dependency;

    public Server(ServerConfig  config) {
        URLClassLoader classLoader = config.getClassLoader();
        this.emitter = config.getEmitter();
        this.port = config.getPort();

        dependency = config.getDependency();
        dependency.add(dependency);
        dependency.add(new ExecutorInvoker(config.getPoolExecutor()));
        dependency.add(config.getRequestSerializer());
        dependency.add(new RoomManager());
        dependency.add(emitter);

        try {
            LoaderServiceManager loaderServiceManager = new LoaderServiceManager(classLoader, dependency);
            loaderServiceManager.loader();

            this.broker = dependency.injectAndReturn(AxBroker.builder().emitter(emitter).build());

            dependency.add(broker);

            dependency.inject(emitter);
            dependency.inject(config.getRequestSerializer());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void viewStartServer() {
        System.out.println(
                "                                                                                                               \n" +
                "███████╗████████╗ █████╗ ██████╗ ████████╗███████╗██████╗     ███████╗███████╗██████╗ ██╗   ██╗███████╗██████╗ \n" +
                "██╔════╝╚══██╔══╝██╔══██╗██╔══██╗╚══██╔══╝██╔════╝██╔══██╗    ██╔════╝██╔════╝██╔══██╗██║   ██║██╔════╝██╔══██╗\n" +
                "███████╗   ██║   ███████║██████╔╝   ██║   █████╗  ██║  ██║    ███████╗█████╗  ██████╔╝██║   ██║█████╗  ██████╔╝\n" +
                "╚════██║   ██║   ██╔══██║██╔══██╗   ██║   ██╔══╝  ██║  ██║    ╚════██║██╔══╝  ██╔══██╗╚██╗ ██╔╝██╔══╝  ██╔══██╗\n" +
                "███████║   ██║   ██║  ██║██║  ██║   ██║   ███████╗██████╔╝    ███████║███████╗██║  ██║ ╚████╔╝ ███████╗██║  ██║\n" +
                "╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ╚══════╝╚═════╝     ╚══════╝╚══════╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝╚═╝  ╚═╝\n" +
                "                                                                                                               ");
        System.out.println("PORT :: " + this.port);
    }

    public CompletableFuture<Channel> start() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ServerChannelInitializer(broker));

                Channel ch = b.bind(port).sync().channel();

                viewStartServer();

                return ch;
            } catch (InterruptedException e) {
                stop();
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Response> publish(Object payload) {
        return emitter.publish(getSessions(), payload);
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private List<Session> getSessions() {
        SessionRegistry sessions = (SessionRegistry) dependency.get(SessionRegistry.class).getFirst();
        return new ArrayList<>(sessions.getSessions().values());
    }
}
