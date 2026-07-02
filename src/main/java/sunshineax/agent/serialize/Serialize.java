package sunshineax.agent.serialize;

import java.io.InputStream;

public interface Serialize<T> {

    byte[] encode(T object);

    T decode(InputStream inputStream);
}
