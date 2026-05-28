package com.locadora_rdt_backend.modules.positions.tracing;

import java.util.function.Supplier;

public interface PositionRepositoryTracingService {

    <T> T traceSearchByName(Supplier<T> supplier);

    <T> T traceFindById(Supplier<T> supplier);

    <T> T traceSave(Supplier<T> supplier);

    void traceDelete(Runnable runnable);

    void traceFlush(Runnable runnable);
}