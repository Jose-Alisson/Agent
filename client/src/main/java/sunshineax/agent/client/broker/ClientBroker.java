package sunshineax.agent.client.broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import sunshineax.agent.annotations.Inject;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.message.Identifier;
import sunshineax.agent.data.message.sub.Response;
import sunshineax.agent.data.session.SessionContext;
import sunshineax.agent.emitter.Emitter;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.factory.manager.impl.RequestResolverManager;
import sunshineax.agent.registry.impl.SessionRegistry;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientBroker implements Broker {

    @Inject
    private SessionRegistry sessionRegistry;

    @Inject
    private Emitter emitter;

    @Inject
    private RequestResolverManager requestResolverManager;

    @Override
    public void register(SessionContext session) {
        sessionRegistry.register(session.getSession().getId(), session);
    }

    @Override
    public void unregister(String agentSession) {
        sessionRegistry.unregister(agentSession);
    }

    @Override
    public Object decode(InputStream stream) {
        return emitter.getSerialize().decode(stream);
    }

    @Override
    public SessionContext context(String session) {
        return sessionRegistry.getSessionContext(session);
    }

    @Override
    public void resolver(ExecutionContext context, Object request) {
        try {
            requestResolverManager.resolver(request.getClass()).dispatch(context, cast(request));
        } catch (Exception e) {
            context.context().ifPresent((c) -> {
                emitter.reply(c.getSession(), Response
                        .builder()
                        .id(UUID.randomUUID().toString())
                        .timestamp(OffsetDateTime.now())
                        .status(ResponseStatus.ERROR)
                        .payload(e.getMessage())
                        .correlation(request instanceof Identifier i ? i.getIdentifier() : null)
                        .build());
            });
        }
    }

    @Override
    public void handlerError(SessionContext sessionContext, Exception e) {
        emitter.getSerialize().encode(
                Response.builder()
                        .status(ResponseStatus.ERROR)
                        .payload("Erro inesperado: " + e.getMessage())
                        .build()
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object request) {
        return (T) request;
    }
}
