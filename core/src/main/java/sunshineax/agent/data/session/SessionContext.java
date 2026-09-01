package sunshineax.agent.data.session;

import lombok.*;
import sunshineax.agent.capability.SessionCapability;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class SessionContext {

    private Session session;

    private Object principal;

    private Set<SessionCapability> capabilities;
}
