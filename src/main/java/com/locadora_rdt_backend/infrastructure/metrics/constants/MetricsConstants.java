package com.locadora_rdt_backend.infrastructure.metrics.constants;

public final class MetricsConstants {

    private MetricsConstants() {
    }

    public static final String APPLICATION_OPERATION_TOTAL = "application.operation.total";
    public static final String APPLICATION_OPERATION_DURATION = "application.operation.duration";
    public static final String APPLICATION_OPERATION_ERROR_TOTAL = "application.operation.error.total";

    public static final String DESCRIPTION_OPERATION_TOTAL = "Total number of application operations";
    public static final String DESCRIPTION_OPERATION_DURATION = "Execution time of application operations";
    public static final String DESCRIPTION_OPERATION_ERROR_TOTAL = "Total number of application operation errors";
}