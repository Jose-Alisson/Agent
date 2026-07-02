package sunshineax.agent.factory.resolver;

import sunshineax.agent.data.SessionContext;

public interface RequestResolver<T> {
    Class<T> resolveType();
    void dispatch(SessionContext sessionContext, T request);
}
