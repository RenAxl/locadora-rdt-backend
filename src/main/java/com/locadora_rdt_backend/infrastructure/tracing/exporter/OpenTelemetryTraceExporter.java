package com.locadora_rdt_backend.infrastructure.tracing.exporter;

import com.locadora_rdt_backend.infrastructure.tracing.model.TraceSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "tracing.exporter.type",
        havingValue = "opentelemetry"
)
public class OpenTelemetryTraceExporter implements TraceExporter {

    private static final Logger logger =
            LoggerFactory.getLogger(OpenTelemetryTraceExporter.class);

    private final TraceExportProperties properties;

    public OpenTelemetryTraceExporter(TraceExportProperties properties) {
        this.properties = properties;
    }

    @Override
    public void export(TraceSpan span) {
        if (!properties.isEnabled() || span == null) {
            return;
        }

        // Vazio por enquanto

        logger.debug(
                "Exporting trace span to OpenTelemetry serviceName={} endpoint={} traceId={} spanId={} parentSpanId={} spanType={} operation={} status={} durationMs={}",
                properties.getServiceName(),
                properties.getEndpoint(),
                span.getTraceId(),
                span.getSpanId(),
                span.getParentSpanId(),
                span.getSpanTypeValue(),
                span.getOperation(),
                span.getStatusValue(),
                span.getDurationMs()
        );
    }
}