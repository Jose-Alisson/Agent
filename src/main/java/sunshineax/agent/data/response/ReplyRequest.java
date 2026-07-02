package sunshineax.agent.data.response;

import sunshineax.agent.data.request.Request;
import sunshineax.agent.enums.ContentType;
import sunshineax.agent.enums.ResponseStatus;

import java.time.OffsetDateTime;

public class ReplyRequest extends Request {

    public ResponseStatus status;
    public String correlationRequest;
    public Object payload;

    public ReplyRequest() {}

    private ReplyRequest(Builder builder) {
        this.status = builder.status;
        this.correlationRequest = builder.correlationRequest;
        this.payload = builder.payload;
    }

    @Override
    public String toString() {
        return "ReplyRequest{" +
                "status=" + status +
                ", correlationRequest='" + correlationRequest + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ResponseStatus status;
        private String correlationRequest;
        private Object payload;

        public Builder status(ResponseStatus status) {
            this.status = status;
            return this;
        }

        public  Builder correlationRequest(String correlationRequest) {
            this.correlationRequest = correlationRequest;
            return this;
        }

        public  Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public ReplyRequest build() {
            return new ReplyRequest(this);
        }
    }
}
