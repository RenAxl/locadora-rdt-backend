package com.locadora_rdt_backend.infrastructure.tracing.context;

import java.util.Optional;

public interface TraceContextProvider {

    void set(TraceContext context);

    Optional<TraceContext> get();

    TraceContext getRequired();

    TraceContext getOrCreate();

    void clear();
}