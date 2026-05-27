package com.locadora_rdt_backend.infrastructure.tracing.service;

import com.locadora_rdt_backend.infrastructure.tracing.constants.TracingConstants;
import com.locadora_rdt_backend.infrastructure.tracing.context.TraceContextHolder;
import com.locadora_rdt_backend.infrastructure.tracing.logger.ApplicationTraceLogger;
import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;
import com.locadora_rdt_backend.shared.context.RequestContext;
import com.locadora_rdt_backend.shared.context.RequestContextProvider;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class DefaultApplicationTracingService implements ApplicationTracingService {

    private final ApplicationTraceLogger traceLogger;
    private final RequestContextProvider requestContextProvider;

    public DefaultApplicationTracingService(
            ApplicationTraceLogger traceLogger,
            RequestContextProvider requestContextProvider
    ) {
        this.traceLogger = traceLogger;
        this.requestContextProvider = requestContextProvider;
    }

    @Override
    public TraceSpan startSpan(
            String module,
            String resource,
            String operation
    ) {
        TraceSpan span = new TraceSpan();

        span.setTraceId(resolveTraceId());
        span.setParentSpanId(resolveParentSpanId());
        span.setModule(normalize(module));
        span.setResource(normalize(resource));
        span.setOperation(normalize(operation));

        TraceContextHolder.push(span);

        traceLogger.logStarted(span);

        return span;
    }

    @Override
    public void finishSpan(TraceSpan span) {
        if (span == null) {
            return;
        }

        span.finishSuccess(calculateDurationInMillis(span));

        traceLogger.logFinished(span);

        TraceContextHolder.pop();
    }

    @Override
    public void finishSpanWithError(
            TraceSpan span,
            Throwable throwable
    ) {
        if (span == null) {
            return;
        }

        span.finishError(throwable, calculateDurationInMillis(span));

        traceLogger.logError(span);

        TraceContextHolder.pop();
    }

    @Override
    public <T> T trace(
            String module,
            String resource,
            String operation,
            Supplier<T> supplier
    ) {
        TraceSpan span = startSpan(module, resource, operation);

        try {
            T result = supplier.get();

            finishSpan(span);

            return result;

        } catch (RuntimeException ex) {
            finishSpanWithError(span, ex);
            throw ex;
        }
    }

    @Override
    public void trace(
            String module,
            String resource,
            String operation,
            Runnable runnable
    ) {
        TraceSpan span = startSpan(module, resource, operation);

        try {
            runnable.run();

            finishSpan(span);

        } catch (RuntimeException ex) {
            finishSpanWithError(span, ex);
            throw ex;
        }
    }

    private String resolveTraceId() {
        String traceIdFromRequestContext = resolveTraceIdFromRequestContext();

        if (hasText(traceIdFromRequestContext)) {
            return traceIdFromRequestContext;
        }

        String traceIdFromMdc = MDC.get(TracingConstants.MDC_TRACE_ID);

        if (hasText(traceIdFromMdc)) {
            return traceIdFromMdc;
        }

        return UUID.randomUUID().toString();
    }

    private String resolveTraceIdFromRequestContext() {
        return requestContextProvider.get()
                .map(RequestContext::getTraceId)
                .orElse(null);
    }

    private String resolveParentSpanId() {
        return TraceContextHolder.getCurrentSpan()
                .map(TraceSpan::getSpanId)
                .orElse(null);
    }

    private long calculateDurationInMillis(TraceSpan span) {
        return TimeUnit.NANOSECONDS.toMillis(
                Duration.between(
                        span.getStartedAt(),
                        Instant.now()
                ).toNanos()
        );
    }

    private String normalize(String value) {
        if (!hasText(value)) {
            return TracingConstants.UNKNOWN;
        }

        return value.trim().toLowerCase();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}