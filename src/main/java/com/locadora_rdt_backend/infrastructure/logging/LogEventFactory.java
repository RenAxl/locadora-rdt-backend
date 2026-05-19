package com.locadora_rdt_backend.infrastructure.logging;

import com.locadora_rdt_backend.shared.context.RequestContext;
import com.locadora_rdt_backend.shared.context.RequestContextProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LogEventFactory {

    private final RequestContextProvider requestContextProvider;

    public LogEventFactory(RequestContextProvider requestContextProvider) {
        this.requestContextProvider = requestContextProvider;
    }

    public LogEvent create(LogLevel level, String message) {
        LogEvent event = new LogEvent();

        event.setTimestamp(Instant.now());
        event.setLevel(level);
        event.setMessage(message);

        requestContextProvider.get().ifPresent(context -> fillFromContext(event, context));

        return event;
    }

    public LogEvent createError(String message, Throwable exception) {
        LogEvent event = create(LogLevel.ERROR, message);

        if (exception != null) {
            event.setExceptionName(exception.getClass().getSimpleName());
            event.setExceptionMessage(exception.getMessage());
        }

        return event;
    }

    private void fillFromContext(LogEvent event, RequestContext context) {
        event.setCorrelationId(context.getCorrelationId());
        event.setTraceId(context.getTraceId());
        event.setUsername(context.getUsername());
        event.setMethod(context.getMethod());
        event.setPath(context.getPath());
        event.setClientIp(context.getClientIp());
        event.setUserAgent(context.getUserAgent());
        event.setDurationMs(context.getDurationMs());
    }
}
