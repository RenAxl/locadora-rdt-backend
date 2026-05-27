package com.locadora_rdt_backend.infrastructure.tracing.service;

import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;

import java.util.function.Supplier;

public interface ApplicationTracingService {

    TraceSpan startSpan(
            String module,
            String resource,
            String operation
    );

    void finishSpan(TraceSpan span);

    void finishSpanWithError(
            TraceSpan span,
            Throwable throwable
    );

    <T> T trace(
            String module,
            String resource,
            String operation,
            Supplier<T> supplier
    );

    void trace(
            String module,
            String resource,
            String operation,
            Runnable runnable
    );
}