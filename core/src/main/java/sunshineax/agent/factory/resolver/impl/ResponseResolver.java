package sunshineax.agent.factory.resolver.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sunshineax.agent.context.ExecutionContext;
import sunshineax.agent.data.message.Identifier;
import sunshineax.agent.data.session.SessionContext;
import sunshineax.agent.data.message.sub.Request;
import sunshineax.agent.data.message.sub.Response;
import sunshineax.agent.enums.ResponseStatus;
import sunshineax.agent.factory.resolver.RequestResolver;

import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseResolver implements RequestResolver<Response> {

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Map<String, CompletableFuture<Response>> transitionMassage = new ConcurrentHashMap<>();

    public CompletableFuture<Response> request(Identifier request) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        if (transitionMassage.putIfAbsent(request.getIdentifier(), future) != null) {
            throw new RuntimeException();
        }
        scheduler(request.getIdentifier(), future, request);
        return future;
    }

    private void scheduler(String id, CompletableFuture<Response> future, Identifier msg) {
        if(msg instanceof Request request) {
            TimeoutRequest timeout = getTimeout(request.getTimeout());
            scheduler.schedule(() -> {
                if(future.complete(getResponseTimeout(request))) {
                    transitionMassage.remove(id);
                }
            }, timeout.time, timeout.unit);
            return;
        }

        scheduler.schedule(() -> {
            if(future.complete(getResponseTimeout(msg))) {
                transitionMassage.remove(id);
            }
        }, 10, TimeUnit.SECONDS);
    }

    private Response getResponseTimeout(Identifier request) {
        return Response.builder().correlation(request.getIdentifier())
                .status(ResponseStatus.TIMEOUT)
                .payload("The request has expired")
                .build();
    }

    private TimeoutRequest getTimeout(String timeout) {
        if (timeout != null) {
            Pattern pattern = Pattern.compile("(\\d+)\\s*([a-zA-Z_]+)");
            Matcher matcher = pattern.matcher(timeout.trim());

            if (matcher.matches()) {
                try {
                    long time = Long.parseLong(matcher.group(1));
                    TimeUnit unit = converterParaTimeUnit(matcher.group(2).toUpperCase());
                    return new TimeoutRequest(time, unit);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }

            throw new RuntimeException();
        }
        return new TimeoutRequest(1, TimeUnit.MINUTES);
    }

    private TimeUnit converterParaTimeUnit(String unidade) {
        return switch (unidade) {
            case "MS", "MILLISECONDS" -> TimeUnit.MILLISECONDS;
            case "S", "SECONDS" -> TimeUnit.SECONDS;
            case "M", "MINUTES" -> TimeUnit.MINUTES;
            case "H", "HOURS" -> TimeUnit.HOURS;
            case "D", "DAYS" -> TimeUnit.DAYS;
            default -> TimeUnit.valueOf(unidade);
        };
    }

    @Override
    public void dispatch(ExecutionContext executionContext, Response request) {
        var completable = transitionMassage.remove(request.getCorrelation());
        if (completable == null) return;
        completable.complete(request);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class TimeoutRequest {
        private long time;
        private TimeUnit unit;
    }
}
