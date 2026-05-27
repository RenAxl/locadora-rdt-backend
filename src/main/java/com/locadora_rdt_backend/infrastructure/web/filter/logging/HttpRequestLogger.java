package com.locadora_rdt_backend.infrastructure.web.filter.logging;

public interface HttpRequestLogger {

    void logRequestStarted(
            String method,
            String path
    );

    void logRequestFinished(
            String method,
            String path,
            Integer httpStatus,
            Long durationMs
    );

    void logRequestFailed(
            String method,
            String path,
            Exception exception
    );
}
