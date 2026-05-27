package com.locadora_rdt_backend.infrastructure.tracing.logger;

import com.locadora_rdt_backend.infrastructure.tracing.constants.TracingConstants;
import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Slf4jApplicationTraceLogger implements ApplicationTraceLogger {

    private static final Logger logger =
            LoggerFactory.getLogger(Slf4jApplicationTraceLogger.class);

    @Override
    public void logStarted(TraceSpan span) {
        logger.info(
                "{} traceId={} spanId={} parentSpanId={} module={} resource={} operation={} status={}",
                TracingConstants.TRACE_STARTED,
                span.getTraceId(),
                span.getSpanId(),
                span.getParentSpanId(),
                span.getModule(),
                span.getResource(),
                span.getOperation(),
                span.getStatus().getValue()
        );
    }

    @Override
    public void logFinished(TraceSpan span) {
        logger.info(
                "{} traceId={} spanId={} parentSpanId={} module={} resource={} operation={} status={} durationMs={}",
                TracingConstants.TRACE_FINISHED,
                span.getTraceId(),
                span.getSpanId(),
                span.getParentSpanId(),
                span.getModule(),
                span.getResource(),
                span.getOperation(),
                span.getStatus().getValue(),
                span.getDurationInMillis()
        );
    }

    @Override
    public void logError(TraceSpan span) {
        logger.error(
                "{} traceId={} spanId={} parentSpanId={} module={} resource={} operation={} status={} durationMs={} errorType={} errorMessage={}",
                TracingConstants.TRACE_ERROR,
                span.getTraceId(),
                span.getSpanId(),
                span.getParentSpanId(),
                span.getModule(),
                span.getResource(),
                span.getOperation(),
                span.getStatus().getValue(),
                span.getDurationInMillis(),
                span.getErrorType(),
                span.getErrorMessage()
        );
    }
}