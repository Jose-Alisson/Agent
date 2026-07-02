package sunshineax.agent.x;

import sunshineax.agent.enums.TypeIntent;
import sunshineax.agent.invoker.AgentCommandInvoker;
import sunshineax.agent.annotations.CapabilityHandler;
import sunshineax.agent.data.RequestContext;
import sunshineax.agent.x.request.Data;

@CapabilityHandler(
        intent = "command",
        type = TypeIntent.COMMAND
)
public class AgentSlaCommand implements AgentCommandInvoker<Data, Data> {

    @Override
    public Data invoker(RequestContext<Data> data) {
        return data.getPayload();
    }
}
