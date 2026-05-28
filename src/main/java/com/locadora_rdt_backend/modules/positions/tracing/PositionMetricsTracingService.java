package com.locadora_rdt_backend.modules.positions.tracing;

import java.util.function.Supplier;

public interface PositionMetricsTracingService {

    <T> T traceMetricsRecord(Supplier<T> supplier);

    void traceMetricsRecord(Runnable runnable);
}