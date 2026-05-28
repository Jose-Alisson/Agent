package com.sunshineax.agent.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InvokerRequest<T> {

    private final T payload;
    private final Map<String, Object> data = new ConcurrentHashMap<>();
    private final InvokerRequest<T> parent;

    public InvokerRequest(T t){
        this(t, null);
    }

    public InvokerRequest(T t, InvokerRequest<T> parent) {
        this.payload = t;
        this.parent = parent;
    }

    public Object get(String key) {
        InvokerRequest<T> current = this;
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

    public InvokerRequest<T> getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return payload.toString() + "x" + data;
    }
}
