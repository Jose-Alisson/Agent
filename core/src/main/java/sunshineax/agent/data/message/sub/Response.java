package sunshineax.agent.data.message.sub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sunshineax.agent.data.message.DataType;
import sunshineax.agent.data.message.Identifier;
import sunshineax.agent.enums.ResponseStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Response implements DataType, Identifier {

    private String id = UUID.randomUUID().toString();
    private OffsetDateTime timestamp = OffsetDateTime.now();
    private ResponseStatus status;
    private String correlation;
    private Object payload;

    @JsonIgnore
    @Override
    public String getIdentifier() {
        return id;
    }
}
