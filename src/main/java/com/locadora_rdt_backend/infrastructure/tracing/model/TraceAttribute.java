package com.locadora_rdt_backend.infrastructure.tracing.model;

public class TraceAttribute {

    private String key;
    private Object value;

    public TraceAttribute() {
    }

    public TraceAttribute(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public static TraceAttribute of(String key, Object value) {
        return new TraceAttribute(key, value);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}