package sunshineax.agent.guard;

import sunshineax.agent.data.session.SessionContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface AuthorizeGuard<T> {

    void authorize(SessionContext sessionContext, T request);

    default Class<?> resolveType() {

        for (Type type : getClass().getGenericInterfaces()){
            if (type instanceof ParameterizedType pt) {
                Type tipoReal = pt.getActualTypeArguments()[0];
                if (tipoReal instanceof Class<?> clazz) {
                    return clazz;
                }
            }
        }
        throw new IllegalArgumentException("Não foi possível extrair o tipo genérico.");
    }
}
