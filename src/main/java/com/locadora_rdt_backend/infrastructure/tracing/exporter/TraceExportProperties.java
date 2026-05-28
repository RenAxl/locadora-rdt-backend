package com.locadora_rdt_backend.infrastructure.tracing.exporter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tracing.exporter")
public class TraceExportProperties {

    private boolean enabled = false;
    private String type = "noop";
    private String endpoint;
    private String serviceName = "locadora-rdt-backend";
    private Double samplingRate = 1.0;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(Double samplingRate) {
        this.samplingRate = samplingRate;
    }
}