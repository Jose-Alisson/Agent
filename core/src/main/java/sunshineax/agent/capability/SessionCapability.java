package sunshineax.agent.capability;

import sunshineax.agent.enums.TypeIntent;

public record SessionCapability(
        String intent,
        TypeIntent type
) {
}
