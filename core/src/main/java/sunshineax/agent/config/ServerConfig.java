package sunshineax.agent.config;

import lombok.*;
import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.emitter.Emitter;
import sunshineax.agent.serialize.request.MessageSerialize;

import java.net.URLClassLoader;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerConfig {
    private int port;
    private int poolExecutor;
    private MessageSerialize requestSerializer;
    private URLClassLoader classLoader;
    private DependencyProvider dependency;
    private Emitter emitter;
}
