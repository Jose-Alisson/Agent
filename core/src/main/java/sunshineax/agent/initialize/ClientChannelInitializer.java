package sunshineax.agent.initialize;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import lombok.Setter;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.channel.handler.AgentChannelHandler;
import sunshineax.agent.enums.Platform;

import java.util.concurrent.TimeUnit;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final WebSocketClientHandshaker handshaker;
    private final AgentChannelHandler agentChannelHandler;

    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap b = new Bootstrap();

    private boolean running;

    @Setter
    private Runnable onConnect;

    @Setter
    private Runnable onDisconnect;

    public ClientChannelInitializer(Broker broker, WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
        b.group(group).channel(NioSocketChannel.class).handler(this);
        agentChannelHandler = new AgentChannelHandler(broker);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        agentChannelHandler.setOnConnect(() -> {
            running = true;
            onConnect.run();
        });

        agentChannelHandler.setOnDisconnect(() -> {
            running = false;
            onDisconnect.run();
        });

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketClientProtocolHandler(handshaker));
        pipeline.addLast(agentChannelHandler);
    }

    public AgentChannelHandler getHandler() {
        return agentChannelHandler;
    }

    public void connect(String host, int port) {
        b.connect(host, port).addListener((ChannelFuture future) -> {
            if(future.isSuccess()) {
                return;
            }
            if(running){
                return;
            }
            future.channel().eventLoop().schedule(() -> connect(host, port), 5, TimeUnit.SECONDS);
        });
    }

    public void close(){
        group.shutdownGracefully();
        running = false;
    }
}
