package com.locadora_rdt_backend.infrastructure.tracing.logger;

import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;

public interface ApplicationTraceLogger {

    void logStarted(TraceSpan span);

    void logFinished(TraceSpan span);

    void logError(TraceSpan span);
}