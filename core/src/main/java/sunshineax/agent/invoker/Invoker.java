package sunshineax.agent.invoker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface Invoker<T> {

    @SuppressWarnings("unchecked")
    default Class<T> resolverType() {
        Type interfaceGenerica = this.getClass().getGenericInterfaces()[0];

        if (interfaceGenerica instanceof ParameterizedType pt) {
            return (Class<T>) pt.getActualTypeArguments()[0];
        }

        throw new IllegalArgumentException("Não foi possível extrair o tipo genérico.");
    }
}
