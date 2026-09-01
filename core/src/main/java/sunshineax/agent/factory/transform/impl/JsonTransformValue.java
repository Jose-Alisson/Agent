package sunshineax.agent.factory.transform.impl;

import sunshineax.agent.factory.transform.TransformValue;
import sunshineax.agent.serialize.request.JacksonMessageSerialize;
import tools.jackson.databind.ObjectMapper;

public class JsonTransformValue implements TransformValue {

    private static final ObjectMapper MAPPER = JacksonMessageSerialize.MAPPER;

    @Override
    public Object transform(Object source, Class<?> type) {
        if(source instanceof String text){
            return transform(MAPPER.readTree(text), type);
        }
        return MAPPER.convertValue(source, type);
    }
}
