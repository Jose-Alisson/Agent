package sunshineax.agent.capability;

public record Capability(
        String intent,
        Class<?> typeRequest
) {
}
