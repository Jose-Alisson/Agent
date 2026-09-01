package sunshineax.agent.registry.impl;

import sunshineax.agent.annotations.RequestType;
import sunshineax.agent.data.message.DataType;
import sunshineax.agent.factory.manager.LoaderService;
import sunshineax.agent.registry.Registry;

import java.net.URLClassLoader;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class TypeRequestRegistry implements Registry<String, Class<?>>, LoaderService {

    private Map<String, Class<?>> types = new ConcurrentHashMap<>();

    @Override
    public void load(URLClassLoader classLoader) {
        ServiceLoader<DataType> serviceLoader = ServiceLoader.load(DataType.class, classLoader);

        for (DataType request : serviceLoader) {
            RequestType requestType = request.getClass().getAnnotation(RequestType.class);

            if (requestType != null) {
                types.put(requestType.value(), request.getClass());
            } else {
                types.put(request.getClass().getSimpleName(), request.getClass());
            }
        }
    }

    @Override
    public void register(String key, Class<?> value) {
        types.put(key, value);
    }

    @Override
    public void unregister(String key) {
        types.remove(key);
    }

    public Class<?> getType(String name) {
        Class<?> type = types.get(name);

        if (type == null) {
            throw new NullPointerException("Type not found: " + name);
        }

        return types.get(name);
    }

    @Override
    public String toString() {
        return "TypeRequestRegistry{" +
                "types=" + types +
                '}';
    }
}
