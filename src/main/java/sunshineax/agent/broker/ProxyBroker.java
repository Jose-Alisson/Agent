package sunshineax.agent.broker;

import sunshineax.agent.broker.external.SupportExternalBrokerProperties;
import sunshineax.agent.data.SessionContext;
import sunshineax.agent.data.request.Discovery;
import sunshineax.agent.data.request.Request;
import sunshineax.agent.data.response.ReplyRequest;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.factory.resolver.impl.Dispatcher;
import sunshineax.agent.emitter.EmitterAdapter;
import sunshineax.agent.enums.ContentType;
import sunshineax.agent.factory.RegistryCapabilityFactory;
import sunshineax.agent.factory.RequestResolverFactory;
import sunshineax.agent.factory.manager.RequestManager;
import sunshineax.agent.registry.AgentSessionRegistry;
import sunshineax.agent.serialize.request.RequestSerialize;
import sunshineax.agent.factory.manager.DiscoveryManager;

import java.util.Set;

public class ProxyBroker implements Broker, SupportExternalBrokerProperties {

    private final AgentSessionRegistry sessionRegistry = new AgentSessionRegistry();
    private final RequestResolverFactory requestResolverFactory;

    private final RequestSerialize requestSerialize;
    private final EmitterAdapter emitterAdapter;

    public ProxyBroker(
            RegistryCapabilityFactory registryCapabilityFactory,
            RequestSerialize requestSerialize
    ) {
        RequestManager requestManager = new RequestManager();
        this.requestSerialize = requestSerialize;
        this.emitterAdapter = new EmitterAdapter(sessionRegistry, requestSerialize, requestManager);

        this.requestResolverFactory = new RequestResolverFactory(Set.of(
                new Dispatcher(registryCapabilityFactory, sessionRegistry, emitterAdapter),
                new DiscoveryManager(emitterAdapter),
                requestManager
        ));
    }

    @Override
    public void register(SessionContext session) {
        sessionRegistry.register(session);
    }

    @Override
    public void unregister(String agentSessionId) {
        sessionRegistry.unregister(agentSessionId);
    }

    @Override
    public void resolver(SessionContext context, Request request) {
        try {
            requestResolverFactory.resolver(request.getClass()).dispatch(context, getRequest(request));
        } catch (Exception e) {
            emitterAdapter.pass(context.getSession(), ReplyRequest
                    .builder()
                    .status(ResponseStatus.ERROR)
                    .correlationRequest(request.getId())
                    .payload(e.getMessage())
                    .build());
        }

        if(request instanceof Discovery){
            System.out.println(sessionRegistry.getSessions());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getRequest(Request request) {
        return (T) request;
    }

    @Override
    public RequestSerialize getRequestSerializer() {
        return requestSerialize;
    }

    @Override
    public AgentSessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    @Override
    public EmitterAdapter getEmitter() {
        return emitterAdapter;
    }
}
