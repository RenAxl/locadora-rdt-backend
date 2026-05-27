package com.locadora_rdt_backend.infrastructure.metrics.service;

import com.locadora_rdt_backend.infrastructure.metrics.builder.MetricsTagsBuilder;
import com.locadora_rdt_backend.infrastructure.metrics.constants.MetricsConstants;
import com.locadora_rdt_backend.infrastructure.metrics.model.MetricOperation;
import com.locadora_rdt_backend.infrastructure.metrics.model.MetricStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class MicrometerApplicationMetricsService implements ApplicationMetricsService {

    private final MeterRegistry meterRegistry;

    public MicrometerApplicationMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void incrementOperationCounter(
            String module,
            String resource,
            MetricOperation operation,
            MetricStatus status
    ) {
        meterRegistry.counter(
                MetricsConstants.APPLICATION_OPERATION_TOTAL,
                MetricsTagsBuilder.create()
                        .module(module)
                        .resource(resource)
                        .operation(operation)
                        .status(status)
                        .build()
        ).increment();
    }

    @Override
    public void incrementErrorCounter(
            String module,
            String resource,
            MetricOperation operation,
            Throwable throwable
    ) {
        meterRegistry.counter(
                MetricsConstants.APPLICATION_OPERATION_ERROR_TOTAL,
                MetricsTagsBuilder.create()
                        .module(module)
                        .resource(resource)
                        .operation(operation)
                        .status(MetricStatus.ERROR)
                        .exception(throwable)
                        .build()
        ).increment();
    }

    @Override
    public void recordExecutionTime(
            String module,
            String resource,
            MetricOperation operation,
            MetricStatus status,
            long durationInMillis
    ) {
        Timer.builder(MetricsConstants.APPLICATION_OPERATION_DURATION)
                .description(MetricsConstants.DESCRIPTION_OPERATION_DURATION)
                .tags(MetricsTagsBuilder.create()
                        .module(module)
                        .resource(resource)
                        .operation(operation)
                        .status(status)
                        .build())
                .register(meterRegistry)
                .record(durationInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> T recordExecutionTime(
            String module,
            String resource,
            MetricOperation operation,
            Supplier<T> supplier
    ) {
        long startTime = System.nanoTime();

        try {
            T result = supplier.get();

            recordExecutionTime(
                    module,
                    resource,
                    operation,
                    MetricStatus.SUCCESS,
                    elapsedMillis(startTime)
            );

            incrementOperationCounter(
                    module,
                    resource,
                    operation,
                    MetricStatus.SUCCESS
            );

            return result;

        } catch (RuntimeException ex) {
            recordExecutionTime(
                    module,
                    resource,
                    operation,
                    MetricStatus.ERROR,
                    elapsedMillis(startTime)
            );

            incrementErrorCounter(
                    module,
                    resource,
                    operation,
                    ex
            );

            throw ex;
        }
    }

    @Override
    public void recordExecutionTime(
            String module,
            String resource,
            MetricOperation operation,
            Runnable runnable
    ) {
        long startTime = System.nanoTime();

        try {
            runnable.run();

            recordExecutionTime(
                    module,
                    resource,
                    operation,
                    MetricStatus.SUCCESS,
                    elapsedMillis(startTime)
            );

            incrementOperationCounter(
                    module,
                    resource,
                    operation,
                    MetricStatus.SUCCESS
            );

        } catch (RuntimeException ex) {
            recordExecutionTime(
                    module,
                    resource,
                    operation,
                    MetricStatus.ERROR,
                    elapsedMillis(startTime)
            );

            incrementErrorCounter(
                    module,
                    resource,
                    operation,
                    ex
            );

            throw ex;
        }
    }

    private long elapsedMillis(long startTime) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
}