package com.locadora_rdt_backend.infrastructure.tracing.model;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class TraceSpan {

    private String traceId;
    private String spanId;
    private String parentSpanId;

    private String module;
    private String resource;
    private String operation;

    private TraceStatus status;

    private Instant startedAt;
    private Instant finishedAt;

    private Long durationInMillis;

    private String errorType;
    private String errorMessage;

    private final Map<String, String> tags = new LinkedHashMap<>();

    public TraceSpan() {
        this.spanId = UUID.randomUUID().toString();
        this.startedAt = Instant.now();
        this.status = TraceStatus.STARTED;
    }

    public void finishSuccess(long durationInMillis) {
        this.status = TraceStatus.SUCCESS;
        this.finishedAt = Instant.now();
        this.durationInMillis = durationInMillis;
    }

    public void finishError(Throwable throwable, long durationInMillis) {
        this.status = TraceStatus.ERROR;
        this.finishedAt = Instant.now();
        this.durationInMillis = durationInMillis;

        if (throwable != null) {
            this.errorType = throwable.getClass().getSimpleName();
            this.errorMessage = throwable.getMessage();
        }
    }

    public void addTag(String key, String value) {
        if (key != null && value != null) {
            this.tags.put(key, value);
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

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public Long getDurationInMillis() {
        return durationInMillis;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, String> getTags() {
        return tags;
    }
}
