package com.locadora_rdt_backend.infrastructure.tracing.model;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class TraceSpan {

    private String traceId;
    private String spanId;
    private String parentSpanId;

    private SpanType spanType;

    private String module;
    private String resource;
    private String operation;

    private TraceStatus status;

    private String correlationId;
    private String username;

    private String method;
    private String path;

    private Instant startedAt;
    private Instant endedAt;

    private Long durationMs;
    private Long durationNs;

    private String errorType;
    private String errorMessage;

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public TraceSpan() {
        this.spanId = UUID.randomUUID().toString();
        this.status = TraceStatus.STARTED;
        this.startedAt = Instant.now();
        this.spanType = SpanType.UNKNOWN;
    }

    public static TraceSpan create(
            String traceId,
            String parentSpanId,
            SpanType spanType,
            String module,
            String resource,
            String operation
    ) {
        TraceSpan span = new TraceSpan();
        span.setTraceId(traceId);
        span.setParentSpanId(parentSpanId);
        span.setSpanType(spanType);
        span.setModule(module);
        span.setResource(resource);
        span.setOperation(operation);
        return span;
    }

    public void finishSuccess() {
        this.status = TraceStatus.SUCCESS;
        this.endedAt = Instant.now();
        calculateDuration();
    }

    public void finishError(Throwable throwable) {
        this.status = TraceStatus.ERROR;
        this.endedAt = Instant.now();
        calculateDuration();

        if (throwable != null) {
            this.errorType = throwable.getClass().getSimpleName();
            this.errorMessage = throwable.getMessage();
        }
    }

    public void addAttribute(String key, Object value) {
        if (key != null && !key.trim().isEmpty() && value != null) {
            this.attributes.put(key, value);
        }
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    private void calculateDuration() {
        if (startedAt != null && endedAt != null) {
            Duration duration = Duration.between(startedAt, endedAt);
            this.durationMs = duration.toMillis();
            this.durationNs = duration.toNanos();
        }
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public SpanType getSpanType() {
        return spanType;
    }

    public void setSpanType(SpanType spanType) {
        this.spanType = spanType;
    }

    public String getSpanTypeValue() {
        if (spanType == null) {
            return SpanType.UNKNOWN.getValue();
        }

        return spanType.getValue();
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public TraceStatus getStatus() {
        return status;
    }

    public void setStatus(TraceStatus status) {
        this.status = status;
    }

    public String getStatusValue() {
        if (status == null) {
            return TraceStatus.UNKNOWN.getValue();
        }

        return status.getValue();
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public Long getDurationNs() {
        return durationNs;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}