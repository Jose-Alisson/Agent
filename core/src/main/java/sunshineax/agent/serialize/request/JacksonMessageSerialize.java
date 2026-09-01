package sunshineax.agent.serialize.request;

import sunshineax.agent.annotations.Inject;
import sunshineax.agent.registry.impl.TypeRequestRegistry;
import sunshineax.agent.serialize.SupportTransformTypes;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.module.blackbird.BlackbirdModule;

import java.io.InputStream;

public class JacksonMessageSerialize implements MessageSerialize {

    public static ObjectMapper MAPPER = JsonMapper.builder()
            .addModules(new BlackbirdModule())
            .build();

    @Inject
    private TypeRequestRegistry registry;

    public JacksonMessageSerialize() {}

    @Override
    public byte[] encode(Object object) {
        ObjectNode objectNode = MAPPER.valueToTree(object);
        objectNode.put("type", object.getClass().getSimpleName());
        return MAPPER.writeValueAsBytes(objectNode);
    }

    @Override
    public Object decode(InputStream stream) {
        JsonNode node = MAPPER.readTree(stream);
        return MAPPER.convertValue(node, registry.getType(node.get("type").asString()));
    }
}
