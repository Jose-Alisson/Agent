package sunshineax.agent.adapter;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import sunshineax.agent.data.session.Session;

public class NettySessionAdapter implements Session {

    private final Channel channel;
    private final String id;

    public NettySessionAdapter(Channel channel) {
        this.channel = channel;
        this.id = channel.id().asLongText();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void sendMessage(String text) {
        if(isOpen()){
            channel.writeAndFlush(new TextWebSocketFrame(text));
        }
    }

    @Override
    public void sendMessage(byte[] message) {
        if (isOpen()) {
            channel.writeAndFlush(
//                    new BinaryWebSocketFrame(Unpooled.wrappedBuffer(message))
                    new TextWebSocketFrame(new String(message))
            );
        }
    }

    @Override
    public void close() {
        this.channel.close();
    }

    @Override
    public boolean isOpen() {
        return channel != null && channel.isActive();
    }

    @Override
    public String toString() {
        return " %s ".formatted(id);
    }
}
