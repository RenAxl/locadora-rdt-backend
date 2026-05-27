package com.locadora_rdt_backend.modules.positions.tracing;

import com.locadora_rdt_backend.infrastructure.tracing.service.ApplicationTracingService;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class PositionTracingServiceImpl implements PositionTracingService {

    private final ApplicationTracingService applicationTracingService;

    public PositionTracingServiceImpl(ApplicationTracingService applicationTracingService) {
        this.applicationTracingService = applicationTracingService;
    }

    @Override
    public <T> T traceFindAll(Supplier<T> supplier) {
        return applicationTracingService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_FIND_ALL,
                supplier
        );
    }

    @Override
    public <T> T traceFindById(Supplier<T> supplier) {
        return applicationTracingService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_FIND_BY_ID,
                supplier
        );
    }

    @Override
    public <T> T traceCreate(Supplier<T> supplier) {
        return applicationTracingService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_CREATE,
                supplier
        );
    }

    @Override
    public <T> T traceUpdate(Supplier<T> supplier) {
        return applicationTracingService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_UPDATE,
                supplier
        );
    }

    @Override
    public void traceDelete(Runnable runnable) {
        applicationTracingService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_DELETE,
                runnable
        );
    }
}
