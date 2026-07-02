package sunshineax.agent.start;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import sunshineax.agent.annotations.CapabilityHandler;
import sunshineax.agent.broker.Broker;
import sunshineax.agent.broker.ProxyBroker;
import sunshineax.agent.broker.external.SupportExternalBrokerProperties;
import sunshineax.agent.capability.SessionCapability;
import sunshineax.agent.data.request.Discovery;
import sunshineax.agent.data.request.Message;
import sunshineax.agent.data.request.Request;
import sunshineax.agent.data.response.ReplyRequest;
import sunshineax.agent.emitter.Emitter;
import sunshineax.agent.emitter.EmitterAdapter;
import sunshineax.agent.enums.TypeIntent;
import sunshineax.agent.factory.RegistryCapabilityFactory;
import sunshineax.agent.initialize.ClientChannelInitializer;
import sunshineax.agent.serialize.request.RequestSerialize;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Agent {

    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap b = new Bootstrap();

    private final RegistryCapabilityFactory capabilityFactory;
    private final RequestSerialize requestSerialize;
    private final String host;
    private final int port;
    private final ClientChannelInitializer initializer;
    private final EmitterAdapter emitterAdapter;

    private Agent(Builder builder) {
        this(builder.uri, builder.registryCapabilityFactory, builder.requestSerialize);
    }

    public Agent(URI uri, RegistryCapabilityFactory registryCapabilityFactory, RequestSerialize serialize) {
        Broker broker = new ProxyBroker(registryCapabilityFactory, serialize);
        this.capabilityFactory = registryCapabilityFactory;
        this.requestSerialize = serialize;
        this.host = uri.getHost();
        this.port = uri.getPort();

        this.initializer = new ClientChannelInitializer(broker, WebSocketClientHandshakerFactory.newHandshaker(
                uri,
                WebSocketVersion.V13,
                null,
                true,
                new DefaultHttpHeaders()
        ));

        SupportExternalBrokerProperties support = (SupportExternalBrokerProperties) broker;
        this.emitterAdapter = support.getEmitter();
    }

    public CompletableFuture<Channel> connect() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(initializer);

                Channel ch = b.connect(host, port).sync().channel();
                initializer.getHandler().getConnectFuture().sync();
                return ch;
            } catch (InterruptedException e) {
                close();
                throw new RuntimeException(e);
            }
        });
    }

    public void close() {
        group.shutdownGracefully();
    }

    public static Builder builder() {
        return new Builder();
    }

    public CompletableFuture<ReplyRequest> publish(Object payload) {
        return emitterAdapter.publish(payload);
    }

    public CompletableFuture<ReplyRequest> publish(Request request) {
        return emitterAdapter.publish(request);
    }

    public CompletableFuture<ReplyRequest> discovery() {
        return publish(Discovery.builder().sessionCapabilities(getCapabilities()).build());
    }

    private Set<SessionCapability> getCapabilities() {
        return capabilityFactory.getCapabilities()
                .parallelStream()
                .map(c -> {
                    CapabilityHandler capabilityHandler = c.typeRequest().getAnnotation(CapabilityHandler.class);
                    return new SessionCapability(c.intent(), capabilityHandler != null ? capabilityHandler.type() : TypeIntent.EVENT);
                })
                .collect(Collectors.toSet());
    }

    public static class Builder {
        private URI uri;
        private RegistryCapabilityFactory registryCapabilityFactory;
        private RequestSerialize requestSerialize;

        public Builder() {
        }

        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder capabilityFactory(RegistryCapabilityFactory registryCapabilityFactory) {
            this.registryCapabilityFactory = registryCapabilityFactory;
            return this;
        }

        public Builder requestSerialize(RequestSerialize requestSerialize) {
            this.requestSerialize = requestSerialize;
            return this;
        }

        public Agent build() {
            return new Agent(this);
        }
    }
}
