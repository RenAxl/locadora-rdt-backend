package com.locadora_rdt_backend.infrastructure.tracing.service;

import com.locadora_rdt_backend.infrastructure.tracing.context.TraceContext;
import com.locadora_rdt_backend.infrastructure.tracing.context.TraceContextFactory;
import com.locadora_rdt_backend.infrastructure.tracing.context.TraceContextHolder;
import com.locadora_rdt_backend.infrastructure.tracing.context.TraceContextProvider;
import com.locadora_rdt_backend.infrastructure.tracing.exporter.TraceExporter;
import com.locadora_rdt_backend.infrastructure.tracing.logger.TraceLogger;
import com.locadora_rdt_backend.infrastructure.tracing.model.SpanType;
import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;
import com.locadora_rdt_backend.shared.context.RequestContext;
import com.locadora_rdt_backend.shared.context.RequestContextProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DefaultSpanLifecycleService implements SpanLifecycleService {

    private static final String UNKNOWN = "unknown";

    private final TraceLogger traceLogger;
    private final RequestContextProvider requestContextProvider;
    private final TraceContextProvider traceContextProvider;
    private final TraceContextFactory traceContextFactory;
    private final TraceExporter traceExporter;

    public DefaultSpanLifecycleService(
            TraceLogger traceLogger,
            RequestContextProvider requestContextProvider,
            TraceContextProvider traceContextProvider,
            TraceContextFactory traceContextFactory,
            TraceExporter traceExporter
    ) {
        this.traceLogger = traceLogger;
        this.requestContextProvider = requestContextProvider;
        this.traceContextProvider = traceContextProvider;
        this.traceContextFactory = traceContextFactory;
        this.traceExporter = traceExporter;
    }

    @Override
    public TraceSpan startSpan(
            SpanType spanType,
            String module,
            String resource,
            String operation
    ) {
        TraceContext traceContext = resolveTraceContext();

        String parentSpanId = TraceContextHolder.getCurrentSpan()
                .map(TraceSpan::getSpanId)
                .orElse(traceContext.getParentSpanId());

        TraceSpan span = TraceSpan.create(
                resolveOrGenerate(traceContext.getTraceId()),
                parentSpanId,
                spanType,
                normalize(module),
                normalize(resource),
                normalize(operation)
        );

        enrichSpanFromContext(span, traceContext);

        TraceContextHolder.push(span);
        traceContextProvider.set(traceContext);

        traceLogger.logStarted(span);

        return span;
    }

    @Override
    public void finishSpan(TraceSpan span) {
        if (span == null) {
            return;
        }

        span.finishSuccess();

        traceLogger.logFinished(span);
        traceExporter.export(span);

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

        span.finishError(throwable);

        traceLogger.logError(span);
        traceExporter.export(span);

        TraceContextHolder.pop();
    }

    private TraceContext resolveTraceContext() {
        return traceContextProvider.get()
                .orElseGet(this::createTraceContextFromRequestContext);
    }

    private TraceContext createTraceContextFromRequestContext() {
        RequestContext requestContext = requestContextProvider.get()
                .orElse(null);

        TraceContext traceContext = traceContextFactory.fromRequestContext(requestContext);

        if (isBlank(traceContext.getTraceId())) {
            traceContext.setTraceId(UUID.randomUUID().toString());
        }

        traceContextProvider.set(traceContext);
        TraceContextHolder.set(traceContext);

        return traceContext;
    }

    private void enrichSpanFromContext(
            TraceSpan span,
            TraceContext traceContext
    ) {
        span.setCorrelationId(traceContext.getCorrelationId());
        span.setUsername(traceContext.getUsername());
        span.setMethod(traceContext.getHttpMethod());
        span.setPath(traceContext.getPath());

        span.addAttribute("clientIp", traceContext.getClientIp());
        span.addAttribute("userAgent", traceContext.getUserAgent());
        span.addAttribute("correlationId", traceContext.getCorrelationId());
    }

    private String resolveOrGenerate(String value) {
        if (isBlank(value)) {
            return UUID.randomUUID().toString();
        }

        return value;
    }

    private String normalize(String value) {
        if (isBlank(value)) {
            return UNKNOWN;
        }

        return value.trim().toLowerCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}