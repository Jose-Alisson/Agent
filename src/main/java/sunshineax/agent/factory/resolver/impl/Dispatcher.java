package sunshineax.agent.factory.resolver.impl;

import sunshineax.agent.data.Session;
import sunshineax.agent.data.SessionContext;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.factory.resolver.RequestResolver;
import sunshineax.agent.invoker.AgentInvoker;
import sunshineax.agent.capability.Capability;
import sunshineax.agent.exception.NotFoundCapabilityException;
import sunshineax.agent.data.RequestContext;
import sunshineax.agent.data.request.Message;
import sunshineax.agent.data.response.ReplyRequest;
import sunshineax.agent.emitter.EmitterAdapter;
import sunshineax.agent.factory.RegistryCapabilityFactory;
import sunshineax.agent.factory.TransformFactory;
import sunshineax.agent.factory.manager.Manager;
import sunshineax.agent.exception.InvokerValidatorException;
import sunshineax.agent.registry.AgentSessionRegistry;

import java.util.Set;

public class Dispatcher implements RequestResolver<Message> {

    private final EmitterAdapter emitterAdapter;
    private final RegistryCapabilityFactory registryCapabilityFactory;
    private final AgentSessionRegistry registry;

    private final TransformFactory transformFactory = new TransformFactory();

    public Dispatcher(RegistryCapabilityFactory registryCapabilityFactory, AgentSessionRegistry registry, EmitterAdapter emitterAdapter) {
        this.registryCapabilityFactory = registryCapabilityFactory;
        this.emitterAdapter = emitterAdapter;
        this.registry = registry;
    }

    @Override
    public Class<Message> resolveType() {
        return Message.class;
    }

    public void dispatch(SessionContext context, Message message) {
        Capability capability = registryCapabilityFactory.getCapability(message.getIntent());

        try {
            if (capability == null) {
                Set<Session> sessions = registry.getSessionsByCapabilityIntent(message.getIntent());

                if (!sessions.isEmpty()) {
                    sessions.parallelStream().forEach(session -> {
                        emitterAdapter.pass(session, message).thenAccept(result -> {
                            emitterAdapter.reply(context.getSession(), result);
                        });
                    });
                    return;
                }

                throw new NotFoundCapabilityException("Not found capability");
            }

            Manager<AgentInvoker<?>, Object, ?> manager = registryCapabilityFactory.getManager(capability);

            Object value = getTransform(message, capability);
            Object result = manager.execute(new RequestContext<>(value));

            replySuccess(context, message, result);

        } catch (Exception e) {
            if (e instanceof InvokerValidatorException validator) {
                replyError(context, message, validator.getConstraints());
                return;
            }
            replyError(context, message, e.getMessage());
        }
    }

    private Object getTransform(Message message, Capability capability) {
        return transformFactory.getTransformValue("JACKSON").transform(message.getPayload(), capability.typeRequest());
    }

    private void replyError(SessionContext context, Message message, Object payload) {
        emitterAdapter.pass(context.getSession(), ReplyRequest
                .builder()
                .status(ResponseStatus.ERROR)
                .correlationRequest(message.getId())
                .payload(payload)
                .build()
        );
    }

    private void replySuccess(SessionContext context, Message message, Object payload) {
        emitterAdapter.pass(context.getSession(), ReplyRequest
                .builder()
                .status(ResponseStatus.SUCCESS)
                .correlationRequest(message.getId())
                .payload(payload)
                .build());
    }
}
