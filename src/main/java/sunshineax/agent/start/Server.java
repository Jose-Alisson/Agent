package sunshineax.agent.start;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.initialize.ServerChannelInitializer;

import java.util.concurrent.CompletableFuture;

public class Server {

    private final int port;
    private final Broker broker;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ServerBootstrap b = new ServerBootstrap();

    public Server(int port, Broker broker) {
        this.port = port;
        this.broker = broker;
    }

    public CompletableFuture<Channel> start() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ServerChannelInitializer(broker));
                return b.bind("0.0.0.0", port).sync().channel();
            } catch (InterruptedException e) {
                stop();
                throw new RuntimeException(e);
            }
        });
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
