package com.locadora_rdt_backend.infrastructure.metrics.service;

import com.locadora_rdt_backend.infrastructure.metrics.model.MetricOperation;
import com.locadora_rdt_backend.infrastructure.metrics.model.MetricStatus;

import java.util.function.Supplier;

public interface ApplicationMetricsService {

    void incrementOperationCounter(
            String module,
            String resource,
            MetricOperation operation,
            MetricStatus status
    );

    void incrementErrorCounter(
            String module,
            String resource,
            MetricOperation operation,
            Throwable throwable
    );

    void recordExecutionTime(
            String module,
            String resource,
            MetricOperation operation,
            MetricStatus status,
            long durationInMillis
    );

    <T> T recordExecutionTime(
            String module,
            String resource,
            MetricOperation operation,
            Supplier<T> supplier
    );

    void recordExecutionTime(
            String module,
            String resource,
            MetricOperation operation,
            Runnable runnable
    );
}