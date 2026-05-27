package com.locadora_rdt_backend.infrastructure.metrics.model;

public enum MetricStatus {

    SUCCESS("success"),
    ERROR("error"),
    NOT_FOUND("not_found"),
    VALIDATION_ERROR("validation_error"),
    FORBIDDEN("forbidden"),
    UNAUTHORIZED("unauthorized"),
    UNKNOWN("unknown");

    private final String value;

    MetricStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}