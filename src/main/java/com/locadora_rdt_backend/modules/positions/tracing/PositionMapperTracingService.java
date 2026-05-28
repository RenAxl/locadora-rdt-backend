package com.locadora_rdt_backend.modules.positions.tracing;

import java.util.function.Supplier;

public interface PositionMapperTracingService {

    <T> T traceToDTO(Supplier<T> supplier);

    <T> T traceToDetailsDTO(Supplier<T> supplier);

    <T> T traceToEntity(Supplier<T> supplier);

    void traceCopyToEntity(Runnable runnable);
}