package sunshineax.agent.registry;

import sunshineax.agent.data.Session;
import sunshineax.agent.data.SessionContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AgentSessionRegistry {

    private final Map<String, SessionContext> sessions = new ConcurrentHashMap<>();

    public void register(SessionContext session) {
        sessions.put(session.getSession().getId(), session);
    }

    public void unregister(String agentSessionId) {
        sessions.remove(agentSessionId);
    }

    public Collection<SessionContext> getSessions() {
        return sessions.values();
    }

    public SessionContext getSessionContext(String agentSessionId) {
        return sessions.get(agentSessionId);
    }

    public Set<Session> getSessionsByCapabilityIntent(String intent) {
        return sessions.values().parallelStream().filter(context ->
                context.getCapabilities().stream().anyMatch(c -> c.intent().equals(intent) )
        ).map(SessionContext::getSession).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return sessions.toString();
    }
}
