package sunshineax.agent.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import lombok.*;
import sunshineax.agent.adapter.NettySessionAdapter;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.context.impl.ClientExecutionContext;
import sunshineax.agent.data.session.SessionContext;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentChannelHandler extends SimpleChannelInboundHandler<Object> {

    private Broker broker;
    private Runnable onConnect;
    private Runnable onDisconnect;

    public AgentChannelHandler(Broker broker) {
        this.broker = broker;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE ||
                evt instanceof WebSocketServerProtocolHandler.HandshakeComplete
        ) {
            broker.register(new SessionContext(new NettySessionAdapter(ctx.channel()), null, Set.of()));
            if (onConnect != null) {
                CompletableFuture.runAsync(onConnect);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = null;

        if (msg instanceof TextWebSocketFrame frame) {
            buffer = frame.content().retain();
        }

        if (msg instanceof BinaryWebSocketFrame frame) {
            buffer = frame.content().retain();
        }

        ByteBuf finalBuffer = buffer;

        CompletableFuture.runAsync(() -> {
            broker.resolver(
                    new ClientExecutionContext(context(ctx)),
                    data(finalBuffer)
            );
        }).whenComplete((result, th) -> {
            if (th instanceof Exception ex) {
                broker.handlerError(context(ctx), ex);
            }
            finalBuffer.release();
        });
    }

    private SessionContext context(ChannelHandlerContext ctx) {
        return broker.context(ctx.channel().id().asLongText());
    }

    private Object data(ByteBuf buffer) {
        return broker.decode(new ByteBufInputStream(buffer));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        broker.unregister(channel.id().asLongText());

        if (onDisconnect != null) {
            CompletableFuture.runAsync(onDisconnect);
        }
    }
}
