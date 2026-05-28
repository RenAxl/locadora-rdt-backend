package com.locadora_rdt_backend.infrastructure.tracing.service;

import com.locadora_rdt_backend.infrastructure.tracing.model.SpanType;
import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class DefaultApplicationTracingService implements ApplicationTracingService {

    private final SpanLifecycleService spanLifecycleService;

    public DefaultApplicationTracingService(
            SpanLifecycleService spanLifecycleService
    ) {
        this.spanLifecycleService = spanLifecycleService;
    }

    @Override
    public TraceSpan startSpan(
            SpanType spanType,
            String module,
            String resource,
            String operation
    ) {
        return spanLifecycleService.startSpan(
                spanType,
                module,
                resource,
                operation
        );
    }

    @Override
    public void finishSpan(TraceSpan span) {
        spanLifecycleService.finishSpan(span);
    }

    @Override
    public void finishSpanWithError(
            TraceSpan span,
            Throwable throwable
    ) {
        spanLifecycleService.finishSpanWithError(span, throwable);
    }

    @Override
    public <T> T trace(
            SpanType spanType,
            String module,
            String resource,
            String operation,
            Supplier<T> supplier
    ) {
        TraceSpan span = startSpan(
                spanType,
                module,
                resource,
                operation
        );

        try {
            T result = supplier.get();

            finishSpan(span);

            return result;

        } catch (RuntimeException ex) {
            finishSpanWithError(span, ex);
            throw ex;
        }
    }

    @Override
    public void trace(
            SpanType spanType,
            String module,
            String resource,
            String operation,
            Runnable runnable
    ) {
        TraceSpan span = startSpan(
                spanType,
                module,
                resource,
                operation
        );

        try {
            runnable.run();

            finishSpan(span);

        } catch (RuntimeException ex) {
            finishSpanWithError(span, ex);
            throw ex;
        }
    }
}