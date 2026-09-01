package sunshineax.agent.factory;

import sunshineax.agent.factory.transform.TransformValue;
import sunshineax.agent.factory.transform.impl.JsonTransformValue;

import java.util.Map;

public class TransformFactory {

    private final Map<String, TransformValue> transforms = Map.ofEntries(
            Map.entry("JACKSON", new JsonTransformValue())
    );

    public TransformValue getTransformValue(String name) {
        TransformValue transform = transforms.get(name);
        if(transform == null) {
            throw new IllegalArgumentException(String.format("Transform with name %s not found", name));
        }
        return transforms.get(name);
    }
}
