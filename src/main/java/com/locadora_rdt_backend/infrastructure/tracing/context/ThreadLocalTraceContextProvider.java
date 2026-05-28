package com.locadora_rdt_backend.infrastructure.tracing.context;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ThreadLocalTraceContextProvider implements TraceContextProvider {

    private static final ThreadLocal<TraceContext> CONTEXT = new ThreadLocal<>();

    @Override
    public void set(TraceContext context) {
        CONTEXT.set(context);
    }

    @Override
    public Optional<TraceContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    @Override
    public TraceContext getRequired() {
        return get().orElseThrow(() ->
                new IllegalStateException("TraceContext não encontrado para a thread atual")
        );
    }

    @Override
    public TraceContext getOrCreate() {
        TraceContext context = CONTEXT.get();

        if (context == null) {
            context = new TraceContext();
            CONTEXT.set(context);
        }

        return context;
    }

    @Override
    public void clear() {
        CONTEXT.remove();
    }
}