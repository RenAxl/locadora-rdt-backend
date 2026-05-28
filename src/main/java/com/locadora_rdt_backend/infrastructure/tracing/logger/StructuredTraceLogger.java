package com.locadora_rdt_backend.infrastructure.tracing.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StructuredTraceLogger implements TraceLogger {

    private static final Logger logger =
            LoggerFactory.getLogger(StructuredTraceLogger.class);

    private final TraceLogEventFactory traceLogEventFactory;
    private final ObjectMapper objectMapper;

    public StructuredTraceLogger(
            TraceLogEventFactory traceLogEventFactory,
            ObjectMapper objectMapper
    ) {
        this.traceLogEventFactory = traceLogEventFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public void logStarted(TraceSpan span) {
        logInfo(traceLogEventFactory.started(span));
    }

    @Override
    public void logFinished(TraceSpan span) {
        logInfo(traceLogEventFactory.finished(span));
    }

    @Override
    public void logError(TraceSpan span) {
        logError(traceLogEventFactory.error(span));
    }

    private void logInfo(TraceLogEvent event) {
        try {
            logger.info(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            logger.info(
                    "TRACE_LOG_SERIALIZATION_ERROR eventType={} traceId={} spanId={}",
                    event.getEventType(),
                    event.getTraceId(),
                    event.getSpanId()
            );
        }
    }

    private void logError(TraceLogEvent event) {
        try {
            logger.error(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            logger.error(
                    "TRACE_LOG_SERIALIZATION_ERROR eventType={} traceId={} spanId={}",
                    event.getEventType(),
                    event.getTraceId(),
                    event.getSpanId()
            );
        }
    }
}
