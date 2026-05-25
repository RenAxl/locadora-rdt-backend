package com.locadora_rdt_backend.infrastructure.logging;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultLogFormatter implements LogFormatter {

    @Override
    public String format(LogEvent event) {

        StringBuilder builder = new StringBuilder();

        append(builder, "timestamp", event.getTimestamp());

        append(builder, "level", event.getLevel());

        append(builder, "message", event.getMessage());

        append(builder, "correlationId", event.getCorrelationId());

        append(builder, "traceId", event.getTraceId());

        append(builder, "username", event.getUsername());

        append(builder, "method", event.getMethod());

        append(builder, "path", event.getPath());

        append(builder, "clientIp", event.getClientIp());

        append(builder, "resource", event.getResource());

        append(builder, "operation", event.getOperation());

        append(builder, "status", event.getStatus());

        append(builder, "durationMs", event.getDurationMs());

        append(builder, "exceptionName", event.getExceptionName());

        append(builder, "exceptionMessage", event.getExceptionMessage());

        append(builder, "className", event.getClassName());

        append(builder, "methodName", event.getMethodName());

        if (event.getAttributes() != null
                && !event.getAttributes().isEmpty()) {

            for (Map.Entry<String, Object> entry :
                    event.getAttributes().entrySet()) {

                append(
                        builder,
                        entry.getKey(),
                        entry.getValue()
                );
            }
        }

        return builder.toString().trim();
    }

    private void append(
            StringBuilder builder,
            String key,
            Object value
    ) {

        if (value != null) {

            builder.append(key)
                    .append("=")
                    .append(value)
                    .append(" ");
        }
    }
}