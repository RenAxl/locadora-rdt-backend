package com.locadora_rdt_backend.infrastructure.web.filter.logging;

import com.locadora_rdt_backend.infrastructure.logging.ApplicationLogger;
import org.springframework.stereotype.Component;

@Component
public class HttpRequestLoggerImpl
        implements HttpRequestLogger {

    private final ApplicationLogger logger;

    private final HttpRequestLogEventFactory eventFactory;

    public HttpRequestLoggerImpl(
            ApplicationLogger logger,
            HttpRequestLogEventFactory eventFactory
    ) {
        this.logger = logger;
        this.eventFactory = eventFactory;
    }

    @Override
    public void logRequestStarted(
            String method,
            String path
    ) {

        HttpRequestLogContext context =
                new HttpRequestLogContext();

        context.setMethod(method);
        context.setPath(path);

        logger.log(
                eventFactory.createInfo(
                        HttpRequestLogMessages.HTTP_REQUEST_STARTED,
                        context
                )
        );
    }

    @Override
    public void logRequestFinished(
            String method,
            String path,
            Integer httpStatus,
            Long durationMs
    ) {

        HttpRequestLogContext context =
                new HttpRequestLogContext();

        context.setMethod(method);
        context.setPath(path);
        context.setHttpStatus(httpStatus);
        context.setDurationMs(durationMs);

        logger.log(
                eventFactory.createInfo(
                        HttpRequestLogMessages.HTTP_REQUEST_FINISHED,
                        context
                )
        );
    }

    @Override
    public void logRequestFailed(
            String method,
            String path,
            Exception exception
    ) {

        HttpRequestLogContext context =
                new HttpRequestLogContext();

        context.setMethod(method);
        context.setPath(path);

        logger.log(
                eventFactory.createError(
                        HttpRequestLogMessages.HTTP_REQUEST_FAILED,
                        context,
                        exception
                )
        );
    }
}