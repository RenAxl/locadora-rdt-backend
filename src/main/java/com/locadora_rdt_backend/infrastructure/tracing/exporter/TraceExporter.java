package com.locadora_rdt_backend.infrastructure.tracing.exporter;

import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;

public interface TraceExporter {

    void export(TraceSpan span);
}