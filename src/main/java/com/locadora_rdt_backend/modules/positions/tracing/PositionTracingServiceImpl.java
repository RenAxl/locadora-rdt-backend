package com.locadora_rdt_backend.modules.positions.tracing;

import com.locadora_rdt_backend.infrastructure.tracing.span.ServiceSpanService;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class PositionTracingServiceImpl implements PositionTracingService {

    private final ServiceSpanService serviceSpanService;

    public PositionTracingServiceImpl(ServiceSpanService serviceSpanService) {
        this.serviceSpanService = serviceSpanService;
    }

    @Override
    public <T> T traceFindAll(Supplier<T> supplier) {
        return serviceSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_FIND_ALL,
                supplier
        );
    }

    @Override
    public <T> T traceFindById(Supplier<T> supplier) {
        return serviceSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_FIND_BY_ID,
                supplier
        );
    }

    @Override
    public <T> T traceCreate(Supplier<T> supplier) {
        return serviceSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_CREATE,
                supplier
        );
    }

    @Override
    public <T> T traceUpdate(Supplier<T> supplier) {
        return serviceSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_UPDATE,
                supplier
        );
    }

    @Override
    public void traceDelete(Runnable runnable) {
        serviceSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_DELETE,
                runnable
        );
    }
}