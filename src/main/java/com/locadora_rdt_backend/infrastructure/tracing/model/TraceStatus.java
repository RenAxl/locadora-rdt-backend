package com.locadora_rdt_backend.infrastructure.tracing.model;

public enum TraceStatus {

    STARTED("started"),
    SUCCESS("success"),
    ERROR("error"),
    CANCELLED("cancelled"),
    UNKNOWN("unknown");

    private final String value;

    TraceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}