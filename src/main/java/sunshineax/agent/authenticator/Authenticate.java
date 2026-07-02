package sunshineax.agent.authenticator;

import sunshineax.agent.data.SessionContext;

public interface Authenticate {

    SessionContext authenticate(String principal);
}
