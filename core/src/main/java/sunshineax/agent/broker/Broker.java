package sunshineax.agent.broker;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.session.SessionContext;
import sunshineax.agent.serialize.Serialize;

import java.io.InputStream;

public interface Broker {

    Object decode(InputStream stream);
    SessionContext context(String session) ;

    void register(SessionContext session);
    void unregister(String agentSession);

    void resolver(ExecutionContext context, Object request);

    void handlerError(SessionContext sessionContext, Exception e);
}
