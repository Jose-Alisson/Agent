package sunshineax.agent.factory.manager;

import sunshineax.agent.capability.SessionCapability;
import sunshineax.agent.data.SessionContext;
import sunshineax.agent.data.request.Discovery;
import sunshineax.agent.data.response.ReplyRequest;
import sunshineax.agent.emitter.EmitterAdapter;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.factory.resolver.RequestResolver;
import sunshineax.agent.registry.AgentSessionRegistry;

import java.util.Set;

public class DiscoveryManager implements RequestResolver<Discovery> {

    private final EmitterAdapter emitterAdapter;

    public DiscoveryManager(EmitterAdapter emitterAdapter) {
        this.emitterAdapter = emitterAdapter;
    }

    @Override
    public Class<Discovery> resolveType() {
        return Discovery.class;
    }

    @Override
    public void dispatch(SessionContext sessionContext, Discovery request) {
        sessionContext.setCapabilities(request.sessionCapabilities);

        emitterAdapter.reply(sessionContext.getSession(), ReplyRequest.builder()
                        .status(ResponseStatus.SUCCESS)
                        .correlationRequest(request.getId())
                .build());
    }
}
