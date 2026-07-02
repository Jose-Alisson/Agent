package sunshineax.agent.x;

import sunshineax.agent.invoker.AgentEventInvoker;
import sunshineax.agent.annotations.CapabilityHandler;
import sunshineax.agent.annotations.Priority;
import sunshineax.agent.data.RequestContext;
import sunshineax.agent.x.request.Data;

@Priority(1)
@CapabilityHandler(
        intent = "event"
)
public class AgentSla implements AgentEventInvoker<Data> {

    @Override
    public void invoker(RequestContext<Data> data) {
        System.out.println("AgentSla invoker...");
    }
}
