package com.locadora_rdt_backend.infrastructure.tracing.span;

import java.util.function.Supplier;

public interface RepositorySpanService {

    <T> T trace(
            String module,
            String resource,
            String operation,
            Supplier<T> supplier
    );

    void trace(
            String module,
            String resource,
            String operation,
            Runnable runnable
    );
}
