package sunshineax.agent.emitter;

import sunshineax.agent.data.request.Request;
import sunshineax.agent.data.response.ReplyRequest;

import java.util.concurrent.CompletableFuture;

public interface Emitter {
    CompletableFuture<ReplyRequest> publish(Request request);
    CompletableFuture<ReplyRequest> publish(Object payload);
}
