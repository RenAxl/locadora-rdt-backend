package com.locadora_rdt_backend.shared.context;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestContext {

    private String requestId;
    private String correlationId;
    private String traceId;

    private String parentTraceId;
    private String parentSpanId;

    private String username;
    private String userId;
    private String sessionId;

    private String method;
    private String path;
    private String requestUri;
    private String queryString;

    private String clientIp;
    private String userAgent;

    private Integer httpStatus;
    private String outcome;

    private Instant startedAt;
    private Instant endedAt;

    private Long durationMs;
    private Long durationNs;

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public RequestContext() {
        this.startedAt = Instant.now();
    }

    public void finish(Integer httpStatus, String outcome) {
        this.httpStatus = httpStatus;
        this.outcome = outcome;
        this.endedAt = Instant.now();

        Duration duration = Duration.between(this.startedAt, this.endedAt);
        this.durationMs = duration.toMillis();
        this.durationNs = duration.toNanos();
    }

    public void addAttribute(String key, Object value) {
        if (key != null && !key.trim().isEmpty() && value != null) {
            this.attributes.put(key, value);
        }
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getParentTraceId() {
        return parentTraceId;
    }

    public void setParentTraceId(String parentTraceId) {
        this.parentTraceId = parentTraceId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
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

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
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

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public Long getDurationNs() {
        return durationNs;
    }

    public void setDurationNs(Long durationNs) {
        this.durationNs = durationNs;
    }
}