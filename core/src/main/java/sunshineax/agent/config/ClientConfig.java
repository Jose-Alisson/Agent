package sunshineax.agent.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.emitter.Emitter;
import sunshineax.agent.serialize.request.MessageSerialize;

import java.net.URI;
import java.net.URLClassLoader;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClientConfig {

    private Emitter emitter;
    private URI uri;
    private MessageSerialize requestSerialize;
    private URLClassLoader urlClassLoader;
    private DependencyProvider dependency;
}
