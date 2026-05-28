package com.locadora_rdt_backend.infrastructure.tracing.logger;

import com.locadora_rdt_backend.infrastructure.tracing.constants.TracingConstants;
import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class TraceLogEventFactory {

    public TraceLogEvent started(TraceSpan span) {
        return create(TracingConstants.TRACE_STARTED, span);
    }

    public TraceLogEvent finished(TraceSpan span) {
        return create(TracingConstants.TRACE_FINISHED, span);
    }

    public TraceLogEvent error(TraceSpan span) {
        return create(TracingConstants.TRACE_ERROR, span);
    }

    private TraceLogEvent create(
            String eventType,
            TraceSpan span
    ) {
        TraceLogEvent event = new TraceLogEvent();

        event.setEventType(eventType);

        event.setTraceId(span.getTraceId());
        event.setSpanId(span.getSpanId());
        event.setParentSpanId(span.getParentSpanId());

        event.setSpanType(span.getSpanTypeValue());

        event.setCorrelationId(span.getCorrelationId());
        event.setUsername(span.getUsername());

        event.setMethod(span.getMethod());
        event.setPath(span.getPath());

        event.setModule(span.getModule());
        event.setResource(span.getResource());
        event.setOperation(span.getOperation());
        event.setStatus(span.getStatusValue());

        event.setDurationMs(span.getDurationMs());
        event.setDurationNs(span.getDurationNs());

        event.setErrorType(span.getErrorType());
        event.setErrorMessage(span.getErrorMessage());

        event.setAttributes(new LinkedHashMap<>(span.getAttributes()));

        return event;
    }
}
