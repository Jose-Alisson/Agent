package sunshineax.agent.provider;

import sunshineax.agent.data.Agent;

public interface AgentProvider {

    Agent loadAgent(String principal);
}
