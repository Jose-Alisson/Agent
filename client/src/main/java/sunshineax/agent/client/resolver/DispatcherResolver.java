package sunshineax.agent.client.resolver;

import sunshineax.agent.annotations.Inject;
import sunshineax.agent.capability.Capability;
import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.RequestContext;
import sunshineax.agent.data.message.sub.Request;
import sunshineax.agent.data.message.sub.Response;
import sunshineax.agent.data.session.SessionContext;
import sunshineax.agent.emitter.Emitter;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.exception.InvokerValidatorException;
import sunshineax.agent.exception.NotFoundCapabilityException;
import sunshineax.agent.factory.TransformFactory;
import sunshineax.agent.factory.manager.impl.RegistryCapabilityManager;
import sunshineax.agent.factory.manager.invoker.Manager;
import sunshineax.agent.factory.resolver.RequestResolver;
import sunshineax.agent.invoker.Invoker;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DispatcherResolver implements RequestResolver<Request> {

    @Inject
    private Emitter emitter;

    @Inject
    private RegistryCapabilityManager registryCapabilityManager;

    private TransformFactory transformFactory = new TransformFactory();

    @Override
    public void dispatch(ExecutionContext context, Request request) {
        Capability capability = registryCapabilityManager.getCapability(getIntent(request.getIntent()));

        context.context().ifPresent((c) -> {
            try {
                if (capability == null) {
                    throw new NotFoundCapabilityException("Not found capability");
                }
                execute(c, capability, request);
            } catch (Exception ex) {
                if (ex instanceof InvokerValidatorException validator) {
                    replyError(c, request, validator.getConstraints());
                    return;
                }
                replyError(c, request, ex.getMessage());
            }
        });
    }

    private void execute(SessionContext context, Capability capability, Request message) {
        Manager<Invoker<?>, Object, ?> manager = registryCapabilityManager.getManager(capability);

        Object value = getTransform(message, capability);
        Object result = manager.execute(new RequestContext<>(value));

        replySuccess(context, message, result);
    }

    private String getIntent(String intent) {
        String[] parts = intent.split(":");
        return parts[parts.length - 1];
    }

    private Object getTransform(Request request, Capability capability) {
        return transformFactory.getTransformValue("JACKSON").transform(request.getPayload(), capability.typeRequest());
    }

    private void replyError(SessionContext context, Request request, Object payload) {
        emitter.reply(context.getSession(), Response
                .builder()
                .status(ResponseStatus.ERROR)
                .id(UUID.randomUUID().toString())
                .timestamp(OffsetDateTime.now())
                .correlation(request.getId())
                .payload(payload)
                .build()
        );
    }

    private void replySuccess(SessionContext context, Request request, Object payload) {
        emitter.reply(context.getSession(), Response
                .builder()
                .id(UUID.randomUUID().toString())
                .timestamp(OffsetDateTime.now())
                .status(ResponseStatus.SUCCESS)
                .correlation(request.getId())
                .payload(payload)
                .build());
    }
}
