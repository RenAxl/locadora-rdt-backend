package com.locadora_rdt_backend.modules.positions.tracing;

public interface PositionValidationTracingService {

    void traceValidateCreate(Runnable runnable);

    void traceValidateUpdate(Runnable runnable);

    void traceValidateDelete(Runnable runnable);
}