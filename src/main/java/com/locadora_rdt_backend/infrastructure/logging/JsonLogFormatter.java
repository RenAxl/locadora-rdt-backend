package com.locadora_rdt_backend.infrastructure.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.infrastructure.logging.sensitive.SensitiveDataMasker;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Primary
@Component
public class JsonLogFormatter implements LogFormatter {

    private final ObjectMapper objectMapper;
    private final SensitiveDataMasker sensitiveDataMasker;

    public JsonLogFormatter(
            ObjectMapper objectMapper,
            SensitiveDataMasker sensitiveDataMasker
    ) {
        this.objectMapper = objectMapper;
        this.sensitiveDataMasker = sensitiveDataMasker;
    }

    @Override
    public String format(LogEvent event) {
        Map<String, Object> log = new LinkedHashMap<>();

        put(log, "timestamp", event.getTimestamp());
        put(log, "level", event.getLevel());
        put(log, "message", event.getMessage());

        put(log, "correlationId", event.getCorrelationId());
        put(log, "traceId", event.getTraceId());
        put(log, "username", event.getUsername());

        put(log, "httpMethod", event.getMethod());
        put(log, "path", event.getPath());
        put(log, "clientIp", event.getClientIp());
        put(log, "userAgent", event.getUserAgent());

        put(log, "resource", event.getResource());
        put(log, "operation", event.getOperation());
        put(log, "status", event.getStatus());

        put(log, "className", event.getClassName());
        put(log, "methodName", event.getMethodName());

        put(log, "durationMs", event.getDurationMs());

        put(log, "exceptionName", event.getExceptionName());
        put(log, "exceptionMessage", event.getExceptionMessage());

        if (event.getAttributes() != null && !event.getAttributes().isEmpty()) {
            log.put(
                    "attributes",
                    maskAttributes(event.getAttributes())
            );
        }

        try {
            return objectMapper.writeValueAsString(log);

        } catch (JsonProcessingException e) {
            return "{\"level\":\"ERROR\",\"message\":\"Failed to format log as JSON\"}";
        }
    }

    private void put(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private Map<String, Object> maskAttributes(
            Map<String, Object> attributes
    ) {

        Map<String, Object> masked =
                new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry :
                attributes.entrySet()) {

            masked.put(
                    entry.getKey(),
                    sensitiveDataMasker.mask(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return masked;
    }
}