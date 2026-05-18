package com.locadora_rdt_backend.shared.context;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class RequestContextFactory {

    public RequestContext create(
            String correlationId,
            String traceId,
            String username,
            String method,
            String path,
            String clientIp,
            String userAgent
    ) {
        RequestContext context = new RequestContext();

        context.setCorrelationId(correlationId);
        context.setTraceId(traceId);
        context.setUsername(username);
        context.setMethod(method);
        context.setPath(path);
        context.setClientIp(clientIp);
        context.setUserAgent(userAgent);
        context.setStartedAt(Instant.now());

        return context;
    }

    public void finish(RequestContext context) {
        Instant endedAt = Instant.now();

        context.setEndedAt(endedAt);

        if (context.getStartedAt() != null) {
            long durationMs = Duration.between(
                    context.getStartedAt(),
                    endedAt
            ).toMillis();

            context.setDurationMs(durationMs);
        }
    }
}