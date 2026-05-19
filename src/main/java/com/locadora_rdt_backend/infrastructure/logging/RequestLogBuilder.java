package com.locadora_rdt_backend.infrastructure.logging;

import org.springframework.stereotype.Component;

@Component
public class RequestLogBuilder {

    private final LogEventFactory logEventFactory;

    public RequestLogBuilder(LogEventFactory logEventFactory) {
        this.logEventFactory = logEventFactory;
    }

    public LogEvent requestStarted() {
        LogEvent event = logEventFactory.create(
                LogLevel.INFO,
                "HTTP request started"
        );

        event.setResource("http-request");
        event.setOperation("REQUEST_STARTED");
        event.setStatus("STARTED");

        return event;
    }

    public LogEvent requestFinished(int statusCode) {
        LogEvent event = logEventFactory.create(
                LogLevel.INFO,
                "HTTP request finished"
        );

        event.setResource("http-request");
        event.setOperation("REQUEST_FINISHED");
        event.setStatus(String.valueOf(statusCode));

        return event;
    }
}