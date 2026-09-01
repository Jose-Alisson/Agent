package sunshineax.agent.context.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.session.SessionContext;

import java.util.Optional;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ClientExecutionContext implements ExecutionContext {

    private SessionContext sessionContext;

    @Override
    public Optional<SessionContext> context() {
        return Optional.of(sessionContext);
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
