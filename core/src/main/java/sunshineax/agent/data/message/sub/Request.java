package sunshineax.agent.data.message.sub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sunshineax.agent.data.message.DataType;
import sunshineax.agent.data.message.Identifier;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Request implements DataType, Identifier {

    private String timeout;
    private String id = UUID.randomUUID().toString();
    private OffsetDateTime timestamp = OffsetDateTime.now();
    private String intent;
    private Object payload;

    @JsonIgnore
    @Override
    public String getIdentifier() {
        return id;
    }
}
