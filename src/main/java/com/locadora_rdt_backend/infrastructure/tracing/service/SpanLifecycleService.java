package com.locadora_rdt_backend.infrastructure.tracing.service;

import com.locadora_rdt_backend.infrastructure.tracing.model.SpanType;
import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;

public interface SpanLifecycleService {

    TraceSpan startSpan(
            SpanType spanType,
            String module,
            String resource,
            String operation
    );

    void finishSpan(TraceSpan span);

    void finishSpanWithError(
            TraceSpan span,
            Throwable throwable
    );
}