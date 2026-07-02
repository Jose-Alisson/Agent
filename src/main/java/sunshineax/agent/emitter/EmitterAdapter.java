package sunshineax.agent.emitter;

import sunshineax.agent.acessor.RuntimeAccessor;
import sunshineax.agent.annotations.Message;
import sunshineax.agent.data.Session;
import sunshineax.agent.data.request.Request;
import sunshineax.agent.data.response.ReplyRequest;
import sunshineax.agent.factory.TransformFactory;
import sunshineax.agent.factory.manager.RequestManager;
import sunshineax.agent.registry.AgentSessionRegistry;
import sunshineax.agent.serialize.request.RequestSerialize;

import java.util.concurrent.CompletableFuture;

public class EmitterAdapter implements Emitter {

    private final RequestSerialize serialize;
    private final AgentSessionRegistry registry;
    private final RequestManager requestManager;
    private final TransformFactory transformFactory = new TransformFactory();

    public EmitterAdapter(AgentSessionRegistry registry, RequestSerialize serialize, RequestManager requestManager) {
        this.registry = registry;
        this.serialize = serialize;
        this.requestManager = requestManager;
    }

    public CompletableFuture<ReplyRequest> pass(Session session, Request request) {
        session.sendMessage(serialize.encode(request));
        return requestManager.request(request);
    }

    public void reply(Session session, Request reply) {
        session.sendMessage(serialize.encode(reply));
    }

    public void send(Request request) {
        registry.getSessions().parallelStream().forEach((session) -> {
            session.getSession().sendMessage(serialize.encode(request));
        });
    }

    @Override
    public CompletableFuture<ReplyRequest> publish(Request request) {
        send(request);
        return requestManager.request(request);
    }

    @Override
    public CompletableFuture<ReplyRequest> publish(Object payload) {
        Message message = payload.getClass().getAnnotation(Message.class);
        if (message != null) {
            Request request = sunshineax.agent.data.request.Message.builder()
                    .intent(message.intent())
                    .payload(payload)
                    .build();
            return publish(request);
        } else if (payload instanceof Request request) {
            return publish(request);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
