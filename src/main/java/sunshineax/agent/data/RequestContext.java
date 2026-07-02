package sunshineax.agent.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestContext<T> {

    private final T payload;
    private final Map<String, Object> data = new ConcurrentHashMap<>();
    private final RequestContext<T> parent;

    public RequestContext(T t) {
        this(t, null);
    }

    public RequestContext(T t, RequestContext<T> parent) {
        this.payload = t;
        this.parent = parent;
    }

    public Object get(String key) {
        RequestContext<T> current = this;
        while (current != null) {
            if (current.data.containsKey(key)) {
                return current.data.get(key);
            }
            current = current.parent;
        }
        return null;
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public T getPayload(){
        return payload;
    }

    public RequestContext<T> getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return payload.toString() + "x" + data;
    }
}
