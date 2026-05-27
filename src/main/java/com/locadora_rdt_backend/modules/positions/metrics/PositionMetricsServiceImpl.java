package com.locadora_rdt_backend.modules.positions.metrics;

import com.locadora_rdt_backend.infrastructure.metrics.model.MetricOperation;
import com.locadora_rdt_backend.infrastructure.metrics.model.MetricStatus;
import com.locadora_rdt_backend.infrastructure.metrics.service.ApplicationMetricsService;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class PositionMetricsServiceImpl implements PositionMetricsService {

    private final ApplicationMetricsService applicationMetricsService;

    public PositionMetricsServiceImpl(ApplicationMetricsService applicationMetricsService) {
        this.applicationMetricsService = applicationMetricsService;
    }

    @Override
    public <T> T recordExecutionTime(
            MetricOperation operation,
            Supplier<T> supplier
    ) {
        return applicationMetricsService.recordExecutionTime(
                PositionMetricsConstants.MODULE,
                PositionMetricsConstants.RESOURCE,
                operation,
                supplier
        );
    }

    @Override
    public void recordExecutionTime(
            MetricOperation operation,
            Runnable runnable
    ) {
        applicationMetricsService.recordExecutionTime(
                PositionMetricsConstants.MODULE,
                PositionMetricsConstants.RESOURCE,
                operation,
                runnable
        );
    }

    @Override
    public void incrementSuccess(MetricOperation operation) {
        applicationMetricsService.incrementOperationCounter(
                PositionMetricsConstants.MODULE,
                PositionMetricsConstants.RESOURCE,
                operation,
                MetricStatus.SUCCESS
        );
    }

    @Override
    public void incrementError(
            MetricOperation operation,
            Throwable throwable
    ) {
        applicationMetricsService.incrementErrorCounter(
                PositionMetricsConstants.MODULE,
                PositionMetricsConstants.RESOURCE,
                operation,
                throwable
        );
    }
}
