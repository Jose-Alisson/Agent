package sunshineax.agent.registry.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sunshineax.agent.data.Agent;
import sunshineax.agent.data.session.Session;
import sunshineax.agent.data.session.SessionContext;
import sunshineax.agent.enums.TypeIntent;
import sunshineax.agent.exception.NotfoundSessionException;
import sunshineax.agent.factory.manager.LoaderService;
import sunshineax.agent.registry.Registry;

import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SessionRegistry implements Registry<String, SessionContext>, LoaderService {

    private Map<String, SessionContext> sessionsContext = new ConcurrentHashMap<>();
    private Map<String, SessionContext> sessionsByName = new ConcurrentHashMap<>();

    @Getter
    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void register(String sessionKey, SessionContext session) {
        sessionsContext.put(sessionKey, session);
    }

    @Override
    public void unregister(String agentSessionId) {
        sessionsContext.remove(agentSessionId);
    }

    public Set<String> getSessionsIdByCapabilityIntent(Set<String> sessions, String intent) {
        return getSessionsById(sessions).parallelStream()
                .filter(context ->
                        context.getCapabilities()
                                .stream()
                                .anyMatch(c -> c.intent().equals(intent))
                )
                .map(s -> s.getSession().getId())
                .collect(Collectors.toSet());
    }

    public boolean unsupportedMoreCommandByIntent(Set<String> sessions, String intent) {
        return getSessionsById(sessions).parallelStream()
                .flatMap(c -> c.getCapabilities().stream())
                .filter(c -> c.intent().equals(intent))
                .filter(c -> c.type() == TypeIntent.COMMAND).count() > 1;
    }

    public List<SessionContext> getSessionsById(Set<String> sessionIds) {
        List<SessionContext> result = new ArrayList<>(sessionIds.size());
        for (String id : sessionIds) {
            SessionContext ctx = sessionsContext.get(id);
            if (ctx != null) {
                result.add(ctx);
            }
        }
        return result;
    }

    public Set<String> getSessionIds() {
        return sessionsContext.keySet();
    }

    public void addSessionsByName(SessionContext sessionContext) {
        Object principal = sessionContext.getPrincipal();

        if (principal instanceof Agent agent) {
            sessionsByName.put(agent.getId(), sessionContext);
            return;
        }

        if (principal instanceof String string) {
            sessionsByName.put(string, sessionContext);
        }
    }

    public SessionContext getSessionByName(String sessionId) {
        SessionContext context = sessionsByName.get(sessionId);

        if (context == null) {
            throw new NotfoundSessionException("Session with name " + sessionId + " not found");
        }

        return sessionsByName.get(sessionId);
    }

    public void removeSessionsByName(String name) {
        sessionsByName.remove(name);
    }

    public SessionContext getSessionContext(String agentSessionId) {
        return sessionsContext.get(agentSessionId);
    }

    @Override
    public void load(URLClassLoader classLoader) {}
}
