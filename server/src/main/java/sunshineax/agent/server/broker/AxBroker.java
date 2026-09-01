package sunshineax.agent.server.broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sunshineax.agent.annotations.Inject;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.Agent;
import sunshineax.agent.data.message.Identifier;
import sunshineax.agent.data.message.sub.Response;
import sunshineax.agent.data.session.Session;
import sunshineax.agent.data.session.SessionContext;
import sunshineax.agent.emitter.Emitter;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.factory.manager.impl.AuthorizeGuardManager;
import sunshineax.agent.factory.manager.impl.RequestResolverManager;
import sunshineax.agent.registry.impl.RoomManager;
import sunshineax.agent.registry.impl.SessionRegistry;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AxBroker implements Broker {

    @Inject
    private SessionRegistry sessionRegistry;

    @Inject
    private RoomManager roomManager;

    @Inject
    private Emitter emitter;

    @Inject
    private RequestResolverManager requestResolverManager;

    @Inject
    private AuthorizeGuardManager authorizeGuardManager;

    @Override
    public void register(SessionContext session) {
        sessionRegistry.register(session.getSession().getId(), session);
    }

    @Override
    public void unregister(String agentSessionId) {

        SessionContext context = sessionRegistry.getSessionContext(agentSessionId);
        Session session = context.getSession();

        if (context.getPrincipal() instanceof Agent agent) {
            sessionRegistry.removeSessionsByName(agent.getId());
        }

        if (context.getPrincipal() instanceof String name) {
            sessionRegistry.removeSessionsByName(name);
        }

        sessionRegistry.unregister(session.getId());
        roomManager.leave(session.getId());
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
    public void resolver(ExecutionContext executionContext, Object request) {

        try {
            try {
                authorizeGuardManager.authorizeChain(executionContext, request);
                requestResolverManager.resolver(request.getClass()).dispatch(executionContext, cast(request));
            } catch (Exception e) {
                executionContext.context().ifPresent((c) -> {
                    emitter.reply(c.getSession(), Response
                            .builder()
                            .id(UUID.randomUUID().toString())
                            .timestamp(OffsetDateTime.now())
                            .status(ResponseStatus.ERROR)
                            .payload(e.getStackTrace())
                            .correlation(request instanceof Identifier i ? i.getIdentifier() : null)
                            .build());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
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
