package com.locadora_rdt_backend.infrastructure.tracing.model;

public enum SpanType {

    HTTP("http"),
    CONTROLLER("controller"),
    SERVICE("service"),
    REPOSITORY("repository"),
    DATABASE("database"),
    VALIDATION("validation"),
    MAPPER("mapper"),
    AUDIT("audit"),
    METRICS("metrics"),
    SECURITY("security"),
    EXTERNAL_API("external_api"),
    UNKNOWN("unknown");

    private final String value;

    SpanType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
