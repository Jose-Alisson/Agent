package sunshineax.agent.factory.manager;

import sunshineax.agent.data.SessionContext;
import sunshineax.agent.data.request.Request;
import sunshineax.agent.data.response.ReplyRequest;
import sunshineax.agent.enums.ContentType;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.factory.resolver.RequestResolver;

import java.util.Map;
import java.util.concurrent.*;

public class RequestManager implements RequestResolver<ReplyRequest> {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, CompletableFuture<ReplyRequest>> transitionMassage = new ConcurrentHashMap<>();

    public CompletableFuture<ReplyRequest> request(Request request) {
        CompletableFuture<ReplyRequest> future = new CompletableFuture<>();
        CompletableFuture<ReplyRequest> existing = transitionMassage.putIfAbsent(request.getId(), future);
        if (existing != null) {
            throw new RuntimeException();
        }
        scheduler(request.getId(), future, request);
        return future;
    }

    private void scheduler(String id, CompletableFuture<ReplyRequest> future, Request request) {
        scheduler.schedule(() -> {
            if (future.complete(
                    ReplyRequest.builder()
                            .correlationRequest(request.getId())
                            .status(ResponseStatus.SUCCESS)
                            .payload("The request has expired")
                            .build())) {
                transitionMassage.remove(id);
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public Class<ReplyRequest> resolveType() {
        return ReplyRequest.class;
    }

    @Override
    public void dispatch(SessionContext sessionContext, ReplyRequest request) {
        var completable = transitionMassage.remove(request.correlationRequest);
        if (completable == null) return;
        completable.complete(request);
    }
}
