package com.locadora_rdt_backend.infrastructure.tracing.context;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class TraceContext {

    private String traceId;
    private String currentSpanId;
    private String parentSpanId;

    private String correlationId;
    private String username;

    private String httpMethod;
    private String path;
    private String clientIp;
    private String userAgent;

    private Instant startedAt;

    private final Map<String, String> attributes = new LinkedHashMap<>();

    public TraceContext() {
        this.startedAt = Instant.now();
    }

    public void addAttribute(String key, String value) {
        if (key != null && value != null) {
            attributes.put(key, value);
        }
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getCurrentSpanId() {
        return currentSpanId;
    }

    public void setCurrentSpanId(String currentSpanId) {
        this.currentSpanId = currentSpanId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
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

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}