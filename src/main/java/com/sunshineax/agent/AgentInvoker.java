package com.sunshineax.agent;

import com.sunshineax.agent.data.InvokerRequest;

public interface AgentInvoker<T> {
    Class<T> type();
    void invoker(InvokerRequest<?> data);
}
