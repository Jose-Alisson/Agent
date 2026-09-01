package sunshineax.agent.emitter;

import lombok.Getter;
import sunshineax.agent.annotations.Inject;
import sunshineax.agent.annotations.RequestWrapper;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.context.impl.ServerExecutionContext;
import sunshineax.agent.data.message.Identifier;
import sunshineax.agent.data.session.Session;
import sunshineax.agent.data.message.sub.Request;
import sunshineax.agent.data.message.sub.Response;
import sunshineax.agent.factory.resolver.impl.ResponseResolver;
import sunshineax.agent.registry.impl.SessionRegistry;
import sunshineax.agent.serialize.request.MessageSerialize;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class Emitter {

    @Inject
    private MessageSerialize serialize;

    @Inject
    private ResponseResolver responseResolver;

    @Inject
    private SessionRegistry sessionRegistry;

    @Inject
    private Broker broker;

    public Emitter() {
    }

    public CompletableFuture<Response> pass(Session session, Request request) {
        reply(session, request);
        return responseResolver.request(request);
    }

    public void reply(Session session, Request reply) {
        session.sendMessage(serialize.encode(reply));
    }

    public void reply(Session session, Object response) {
        session.sendMessage(serialize.encode(response));
    }

    public void send(List<Session> sessions, Object request) {
        CompletableFuture.runAsync(() -> {
            for (Session session : sessions) {
                reply(session, request);
            }
        });
    }

    public CompletableFuture<Response> publish(List<Session> sessions, Object payload) {
        RequestWrapper message = payload.getClass().getAnnotation(RequestWrapper.class);

        if (message != null) {
            Request request = getMessage(message, payload);
            send(sessions, request);
            return responseResolver.request(request);
        }

        if (payload instanceof Identifier identifier) {
            send(sessions, payload);
            return responseResolver.request(identifier);
        }

        throw new RuntimeException();
    }

    private Request getMessage(RequestWrapper requestWrapper, Object payload) {
        return Request.builder()
                .intent(requestWrapper.intent())
                .payload(payload)
                .build();
    }

    public CompletableFuture<Object> publish(Object payload) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        broker.resolver(new ServerExecutionContext(future), payload);
        return future;
    }
}
