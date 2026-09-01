package sunshineax.agent.data.message.sub;

import lombok.*;
import sunshineax.agent.data.message.DataType;
import sunshineax.agent.data.message.Identifier;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Authenticate implements DataType, Identifier {

    private String id = UUID.randomUUID().toString();
    private String agent;
    private String secret;

    @Override
    public String getIdentifier() {
        return id;
    }
}
