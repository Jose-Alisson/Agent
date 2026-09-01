package sunshineax.agent.context.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.session.SessionContext;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerExecutionContext implements ExecutionContext {

    CompletableFuture<Object> resultChannelFuture = new CompletableFuture<>();

    @Override
    public Optional<SessionContext> context() {
        return Optional.empty();
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
