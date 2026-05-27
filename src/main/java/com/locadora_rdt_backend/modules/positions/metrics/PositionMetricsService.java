package com.locadora_rdt_backend.modules.positions.metrics;

import com.locadora_rdt_backend.infrastructure.metrics.model.MetricOperation;

import java.util.function.Supplier;

public interface PositionMetricsService {

    <T> T recordExecutionTime(
            MetricOperation operation,
            Supplier<T> supplier
    );

    void recordExecutionTime(
            MetricOperation operation,
            Runnable runnable
    );

    void incrementSuccess(
            MetricOperation operation
    );

    void incrementError(
            MetricOperation operation,
            Throwable throwable
    );
}