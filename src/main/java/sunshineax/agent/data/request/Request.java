package sunshineax.agent.data.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sunshineax.agent.data.response.ReplyRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Discovery.class, name = "DISCOVERY"),
        @JsonSubTypes.Type(value = Message.class, names = "MESSAGE"),
        @JsonSubTypes.Type(value = ReplyRequest.class, name = "REPLY")
})
public class Request {

    private String id = UUID.randomUUID().toString();
    private OffsetDateTime timestamp;

    public Request(){}

    public Request(String id, OffsetDateTime timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
