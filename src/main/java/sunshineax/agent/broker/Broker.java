package sunshineax.agent.broker;

import sunshineax.agent.data.Session;
import sunshineax.agent.data.SessionContext;
import sunshineax.agent.data.request.Request;

public interface Broker {

    void register(SessionContext session);
    void unregister(String agentSession);
    void resolver(SessionContext context, Request request);
}
