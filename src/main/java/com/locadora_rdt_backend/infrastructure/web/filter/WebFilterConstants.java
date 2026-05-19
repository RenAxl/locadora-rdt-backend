package com.locadora_rdt_backend.infrastructure.web.filter;

public final class WebFilterConstants {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    public static final String UNKNOWN = "UNKNOWN";
    public static final String ANONYMOUS = "anonymous";

    private WebFilterConstants() {
    }
}