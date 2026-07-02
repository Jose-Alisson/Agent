package sunshineax.agent.data.request;

import sunshineax.agent.enums.ContentType;

public class Message extends Request {

    private String intent;
    private Object payload;

    public Message() {}

    public Message(ContentType type, String intent) {
        this.intent = intent;
    }

    public Message(ContentType type, String intent, Object payload) {
        this.intent = intent;
        this.payload = payload;
    }

    private Message(Builder builder) {
        this.intent = builder.intent;
        this.payload = builder.payload;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "intent='" + intent + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String intent;
        private Object payload;

        public Builder(){}

        public Builder intent(String intent) {
            this.intent = intent;
            return this;
        }

        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
