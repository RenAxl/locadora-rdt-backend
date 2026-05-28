package com.locadora_rdt_backend.modules.positions.tracing;

import com.locadora_rdt_backend.infrastructure.tracing.span.MetricsSpanService;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class PositionMetricsTracingServiceImpl implements PositionMetricsTracingService {

    private final MetricsSpanService metricsSpanService;

    public PositionMetricsTracingServiceImpl(MetricsSpanService metricsSpanService) {
        this.metricsSpanService = metricsSpanService;
    }

    @Override
    public <T> T traceMetricsRecord(Supplier<T> supplier) {
        return metricsSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_METRICS_RECORD,
                supplier
        );
    }

    @Override
    public void traceMetricsRecord(Runnable runnable) {
        metricsSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_METRICS_RECORD,
                runnable
        );
    }
}