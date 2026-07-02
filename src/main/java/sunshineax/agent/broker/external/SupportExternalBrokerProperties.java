package sunshineax.agent.broker.external;

import sunshineax.agent.emitter.EmitterAdapter;
import sunshineax.agent.registry.AgentSessionRegistry;
import sunshineax.agent.serialize.request.RequestSerialize;

public interface SupportExternalBrokerProperties {
    EmitterAdapter getEmitter();
    RequestSerialize getRequestSerializer();
    AgentSessionRegistry getSessionRegistry();
}
