package com.locadora_rdt_backend.infrastructure.logging;

import org.springframework.stereotype.Component;

@Component
public class ErrorLogBuilder {

    private final LogEventFactory logEventFactory;

    public ErrorLogBuilder(LogEventFactory logEventFactory) {
        this.logEventFactory = logEventFactory;
    }

    public LogEvent unexpectedError(Throwable exception) {
        LogEvent event = logEventFactory.createError(
                "Unexpected application error",
                exception
        );

        event.setResource("application");
        event.setOperation("UNEXPECTED_ERROR");
        event.setStatus("ERROR");

        return event;
    }

    public LogEvent businessError(String resource, String operation, Throwable exception) {
        LogEvent event = logEventFactory.createError(
                "Business operation failed",
                exception
        );

        event.setResource(resource);
        event.setOperation(operation);
        event.setStatus("ERROR");

        return event;
    }
}