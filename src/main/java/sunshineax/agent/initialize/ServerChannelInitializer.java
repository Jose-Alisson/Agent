package sunshineax.agent.initialize;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.channel.handler.AgentChannelHandler;
import sunshineax.agent.enums.Platform;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Broker broker;

    public ServerChannelInitializer(Broker broker) {
        this.broker = broker;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/agent", null, true));
        pipeline.addLast(new AgentChannelHandler(broker, Platform.SERVER));
    }
}
