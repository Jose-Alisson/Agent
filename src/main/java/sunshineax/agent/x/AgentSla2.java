package sunshineax.agent.x;

import sunshineax.agent.invoker.AgentEventInvoker;
import sunshineax.agent.annotations.CapabilityHandler;
import sunshineax.agent.annotations.Priority;
import sunshineax.agent.data.RequestContext;
import sunshineax.agent.x.request.Data;
import sunshineax.agent.x.request.Data2;

@Priority(2)
@CapabilityHandler(
        intent = "event"
)
public class AgentSla2 implements AgentEventInvoker<Data> {

    @Override
    public void invoker(RequestContext<Data> data2) {
        System.out.println("AgentSla2.invoker");
    }
}
