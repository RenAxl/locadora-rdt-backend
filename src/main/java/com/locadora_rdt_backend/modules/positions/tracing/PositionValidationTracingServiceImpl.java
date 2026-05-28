package com.locadora_rdt_backend.modules.positions.tracing;

import com.locadora_rdt_backend.infrastructure.tracing.span.ValidationSpanService;
import org.springframework.stereotype.Service;

@Service
public class PositionValidationTracingServiceImpl implements PositionValidationTracingService {

    private final ValidationSpanService validationSpanService;

    public PositionValidationTracingServiceImpl(ValidationSpanService validationSpanService) {
        this.validationSpanService = validationSpanService;
    }

    @Override
    public void traceValidateCreate(Runnable runnable) {
        validationSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_VALIDATE_CREATE,
                runnable
        );
    }

    @Override
    public void traceValidateUpdate(Runnable runnable) {
        validationSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_VALIDATE_UPDATE,
                runnable
        );
    }

    @Override
    public void traceValidateDelete(Runnable runnable) {
        validationSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_VALIDATE_DELETE,
                runnable
        );
    }
}