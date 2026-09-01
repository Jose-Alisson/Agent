package sunshineax.agent.data.message.sub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sunshineax.agent.capability.SessionCapability;
import sunshineax.agent.data.message.DataType;
import sunshineax.agent.data.message.Identifier;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Discovery implements DataType , Identifier {

    private String id = UUID.randomUUID().toString();
    public Set<SessionCapability> sessionCapabilities;

    @JsonIgnore
    @Override
    public String getIdentifier() {
        return id;
    }
}
