package sunshineax.agent.serialize.request;

import sunshineax.agent.data.request.Request;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.module.blackbird.BlackbirdModule;

import java.io.InputStream;

public class JacksonRequestSerialize implements RequestSerialize {

    public static final ObjectMapper MAPPER = JsonMapper.builder().addModules(new BlackbirdModule()).build();
    private static final ObjectReader REUSABLE_READER = MAPPER.readerFor(Request.class);
    private static final ObjectWriter REUSABLE_WRITER = MAPPER.writerFor(Request.class);

    @Override
    public byte[] encode(Request object) {
        return REUSABLE_WRITER.writeValueAsBytes(object);
    }

    @Override
    public Request decode(InputStream stream) {
        return REUSABLE_READER.readValue(stream);
    }
}
