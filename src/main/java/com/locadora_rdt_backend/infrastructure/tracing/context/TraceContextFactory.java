package com.locadora_rdt_backend.infrastructure.tracing.context;

import com.locadora_rdt_backend.shared.context.RequestContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TraceContextFactory {

    public TraceContext fromRequestContext(RequestContext requestContext) {
        TraceContext traceContext = new TraceContext();

        if (requestContext == null) {
            traceContext.setTraceId(UUID.randomUUID().toString());
            return traceContext;
        }

        traceContext.setTraceId(resolveOrGenerate(requestContext.getTraceId()));
        traceContext.setCorrelationId(requestContext.getCorrelationId());
        traceContext.setUsername(requestContext.getUsername());

        traceContext.setHttpMethod(requestContext.getMethod());
        traceContext.setPath(requestContext.getPath());
        traceContext.setClientIp(requestContext.getClientIp());
        traceContext.setUserAgent(requestContext.getUserAgent());

        return traceContext;
    }

    private String resolveOrGenerate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return UUID.randomUUID().toString();
        }

        return value;
    }
}