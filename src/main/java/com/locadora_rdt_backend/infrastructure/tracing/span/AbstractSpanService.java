package com.locadora_rdt_backend.infrastructure.tracing.span;

import com.locadora_rdt_backend.infrastructure.tracing.model.SpanType;
import com.locadora_rdt_backend.infrastructure.tracing.service.ApplicationTracingService;

import java.util.function.Supplier;

public abstract class AbstractSpanService {

    private final ApplicationTracingService applicationTracingService;

    protected AbstractSpanService(ApplicationTracingService applicationTracingService) {
        this.applicationTracingService = applicationTracingService;
    }

    protected abstract SpanType getSpanType();

    public <T> T trace(
            String module,
            String resource,
            String operation,
            Supplier<T> supplier
    ) {
        return applicationTracingService.trace(
                getSpanType(),
                module,
                resource,
                operation,
                supplier
        );
    }

    public void trace(
            String module,
            String resource,
            String operation,
            Runnable runnable
    ) {
        applicationTracingService.trace(
                getSpanType(),
                module,
                resource,
                operation,
                runnable
        );
    }
}
