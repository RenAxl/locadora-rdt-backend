package com.locadora_rdt_backend.infrastructure.web.filter.logging;

import com.locadora_rdt_backend.infrastructure.logging.LogEvent;
import com.locadora_rdt_backend.infrastructure.logging.LogEventFactory;
import com.locadora_rdt_backend.infrastructure.logging.LogLevel;
import org.springframework.stereotype.Component;

@Component
public class HttpRequestLogEventFactory {

    private static final String RESOURCE = "http";

    private final LogEventFactory logEventFactory;

    public HttpRequestLogEventFactory(
            LogEventFactory logEventFactory
    ) {
        this.logEventFactory = logEventFactory;
    }

    public LogEvent createInfo(
            String message,
            HttpRequestLogContext context
    ) {
        LogEvent event = logEventFactory.create(
                LogLevel.INFO,
                message
        );

        fill(event, context);

        return event;
    }

    public LogEvent createError(
            String message,
            HttpRequestLogContext context,
            Throwable exception
    ) {
        LogEvent event = logEventFactory.createError(
                message,
                exception
        );

        fill(event, context);

        return event;
    }

    private void fill(
            LogEvent event,
            HttpRequestLogContext context
    ) {
        event.setResource(RESOURCE);

        event.setOperation(
                HttpRequestLogOperations.REQUEST
        );

        if (context == null) {
            return;
        }

        event.setStatus(context.getStatus());

        event.setDurationMs(
                context.getDurationMs()
        );

        if (context.getHttpStatus() != null) {
            event.addAttribute(
                    "httpStatus",
                    context.getHttpStatus()
            );
        }
    }
}