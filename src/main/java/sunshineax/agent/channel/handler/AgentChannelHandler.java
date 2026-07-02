package sunshineax.agent.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import sunshineax.agent.adapter.NettySessionAdapter;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.broker.external.SupportExternalBrokerProperties;
import sunshineax.agent.data.SessionContext;
import sunshineax.agent.data.request.Message;
import sunshineax.agent.data.request.Request;
import sunshineax.agent.data.response.ReplyRequest;
import sunshineax.agent.enums.Platform;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.serialize.request.RequestSerialize;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AgentChannelHandler extends SimpleChannelInboundHandler<Object> {

    private final Broker broker;
    private final Platform platform;

    private ChannelPromise connectFuture;

    public AgentChannelHandler(Broker broker, Platform platform) {
        this.broker = broker;
        this.platform = platform;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE ||
                evt instanceof WebSocketServerProtocolHandler.HandshakeComplete
        ) {
            broker.register(new SessionContext(new NettySessionAdapter(ctx.channel()), "", Set.of()));
            connectFuture.trySuccess();
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
            try {
                Channel channel = ctx.channel();
                SupportExternalBrokerProperties external = broker instanceof SupportExternalBrokerProperties ?
                        (SupportExternalBrokerProperties) broker : null;

                if (external == null) return;

                Request request = external
                        .getRequestSerializer()
                        .decode(new ByteBufInputStream(finalBuffer));

                request.setTimestamp(OffsetDateTime.now());

                broker.resolver(
                        external
                                .getSessionRegistry()
                                .getSessionContext(
                                        channel.id().asLongText()
                                ), request
                );
            } catch (Exception e){
                replyError(ctx.channel(), e);
            } finally {
                finalBuffer.release();
            }
        });
    }

    private void replyError(Channel channel, Exception e) {
        SupportExternalBrokerProperties external = broker instanceof SupportExternalBrokerProperties ?
                (SupportExternalBrokerProperties) broker : null;

        if (external != null && external.getRequestSerializer() != null) {
            channel.writeAndFlush(
                    new BinaryWebSocketFrame(
                            Unpooled.wrappedBuffer(external.getRequestSerializer()
                                            .encode(
                                                    ReplyRequest.builder()
                                                            .status(ResponseStatus.ERROR)
                                                            .payload(e.getMessage())
                                                            .build()
                                            ))));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        connectFuture = ctx.newPromise();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        broker.unregister(channel.id().asLongText());
    }

    public ChannelPromise getConnectFuture() {
        return connectFuture;
    }
}
