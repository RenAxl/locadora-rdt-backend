package com.locadora_rdt_backend.modules.positions.tracing;

public interface PositionAuditTracingService {

    void traceAuditCreate(Runnable runnable);

    void traceAuditUpdate(Runnable runnable);

    void traceAuditDelete(Runnable runnable);
}