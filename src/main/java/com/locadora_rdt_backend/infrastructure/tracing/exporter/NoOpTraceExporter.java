package com.locadora_rdt_backend.infrastructure.tracing.exporter;

import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "tracing.exporter.type",
        havingValue = "noop",
        matchIfMissing = true
)
public class NoOpTraceExporter implements TraceExporter {

    @Override
    public void export(TraceSpan span) {
        // Vazio por enquanto.
    }
}