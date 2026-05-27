package com.locadora_rdt_backend.infrastructure.metrics.model;

public enum MetricOperation {

    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    FIND_BY_ID("find_by_id"),
    FIND_ALL("find_all"),
    VALIDATE("validate"),
    AUDIT("audit"),
    UNKNOWN("unknown");

    private final String value;

    MetricOperation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}