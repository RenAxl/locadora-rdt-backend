package com.locadora_rdt_backend.modules.positions.tracing;

import java.util.function.Supplier;

public interface PositionTracingService {

    <T> T traceFindAll(Supplier<T> supplier);

    <T> T traceFindById(Supplier<T> supplier);

    <T> T traceCreate(Supplier<T> supplier);

    <T> T traceUpdate(Supplier<T> supplier);

    void traceDelete(Runnable runnable);
}
