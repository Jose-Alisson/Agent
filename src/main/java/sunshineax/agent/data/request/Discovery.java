package sunshineax.agent.data.request;

import sunshineax.agent.capability.SessionCapability;

import java.util.Set;

public class Discovery extends Request {

    public Set<SessionCapability> sessionCapabilities;

    private Discovery(Builder builder) {
        this.sessionCapabilities = builder.sessionCapabilities;
    }

    public Discovery() {}

    public Discovery(Set<SessionCapability> sessionCapabilities) {
        this.sessionCapabilities = sessionCapabilities;
    }

    @Override
    public String toString() {
        return "Discovery{" +
                "sessionCapabilities=" + sessionCapabilities +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Set<SessionCapability> sessionCapabilities;

        public Builder() {}

        public Builder sessionCapabilities(Set<SessionCapability> sessionCapabilities) {
            this.sessionCapabilities = sessionCapabilities;
            return this;
        }

        public Discovery build() {
            return new Discovery(this);
        }
    }
}
