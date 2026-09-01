package sunshineax.agent.factory.resolver;

import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.session.SessionContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface RequestResolver<T> {

    void dispatch(ExecutionContext executionContext, T request);

    @SuppressWarnings("unchecked")
    default Class<T> resolveType() {
        Type interfaceGenerica = this.getClass().getGenericInterfaces()[0];

        if (interfaceGenerica instanceof ParameterizedType pt) {
            return (Class<T>) pt.getActualTypeArguments()[0];
        }
        throw new IllegalArgumentException("Cannot resolve type of RequestResolver");
    }
}
