package sunshineax.agent.data;

import sunshineax.agent.capability.SessionCapability;

import java.util.Set;

public class SessionContext {

    private Session session;

    private String principal;

    private Set<SessionCapability> capabilities;

    public SessionContext() {}

    public SessionContext(Session session, String principal, Set<SessionCapability> capabilities) {
        this.session = session;
        this.principal = principal;
        this.capabilities = capabilities;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Set<SessionCapability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<SessionCapability> capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public String toString() {
        return " %s %s %s ".formatted(session,  principal, capabilities);
    }
}
