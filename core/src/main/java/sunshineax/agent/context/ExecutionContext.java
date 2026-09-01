package sunshineax.agent.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sunshineax.agent.data.session.SessionContext;

import java.util.Optional;

public interface ExecutionContext {
    Optional<SessionContext> context();
    boolean isClient();
}
