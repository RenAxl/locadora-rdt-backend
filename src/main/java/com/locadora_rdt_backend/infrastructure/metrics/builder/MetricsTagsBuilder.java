package com.locadora_rdt_backend.infrastructure.metrics.builder;

import com.locadora_rdt_backend.infrastructure.metrics.constants.MetricsTagConstants;
import com.locadora_rdt_backend.infrastructure.metrics.model.MetricOperation;
import com.locadora_rdt_backend.infrastructure.metrics.model.MetricStatus;
import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.List;

public class MetricsTagsBuilder {

    private final List<Tag> tags = new ArrayList<>();

    private MetricsTagsBuilder() {
    }

    public static MetricsTagsBuilder create() {
        return new MetricsTagsBuilder();
    }

    public MetricsTagsBuilder module(String module) {
        tags.add(Tag.of(MetricsTagConstants.MODULE, normalize(module)));
        return this;
    }

    public MetricsTagsBuilder resource(String resource) {
        tags.add(Tag.of(MetricsTagConstants.RESOURCE, normalize(resource)));
        return this;
    }

    public MetricsTagsBuilder operation(MetricOperation operation) {
        String value = operation != null ? operation.getValue() : MetricsTagConstants.UNKNOWN;
        tags.add(Tag.of(MetricsTagConstants.OPERATION, value));
        return this;
    }

    public MetricsTagsBuilder status(MetricStatus status) {
        String value = status != null ? status.getValue() : MetricsTagConstants.UNKNOWN;
        tags.add(Tag.of(MetricsTagConstants.STATUS, value));
        return this;
    }

    public MetricsTagsBuilder exception(Throwable throwable) {
        String value = throwable != null ? throwable.getClass().getSimpleName() : MetricsTagConstants.UNKNOWN;
        tags.add(Tag.of(MetricsTagConstants.EXCEPTION, value));
        return this;
    }

    public MetricsTagsBuilder exception(String exceptionName) {
        tags.add(Tag.of(MetricsTagConstants.EXCEPTION, normalize(exceptionName)));
        return this;
    }

    public Iterable<Tag> build() {
        return new ArrayList<>(tags);
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return MetricsTagConstants.UNKNOWN;
        }

        return value.trim().toLowerCase();
    }
}
