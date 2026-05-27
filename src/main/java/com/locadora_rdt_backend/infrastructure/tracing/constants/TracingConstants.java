package com.locadora_rdt_backend.infrastructure.tracing.constants;

public final class TracingConstants {

    private TracingConstants() {
    }

    public static final String MDC_TRACE_ID = "traceId";

    public static final String UNKNOWN = "unknown";

    public static final String TRACE_STARTED = "TRACE_STARTED";
    public static final String TRACE_FINISHED = "TRACE_FINISHED";
    public static final String TRACE_ERROR = "TRACE_ERROR";
}