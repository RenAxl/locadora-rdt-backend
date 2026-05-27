package com.locadora_rdt_backend.infrastructure.web.filter.logging;

public final class HttpRequestLogStatus {

    private HttpRequestLogStatus() {
    }

    public static final String STARTED =
            "STARTED";

    public static final String SUCCESS =
            "SUCCESS";

    public static final String ERROR =
            "ERROR";
}
