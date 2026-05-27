package com.locadora_rdt_backend.infrastructure.web.filter.logging;

public final class HttpRequestLogMessages {

    private HttpRequestLogMessages() {
    }

    public static final String HTTP_REQUEST_STARTED =
            "HTTP request started";

    public static final String HTTP_REQUEST_FINISHED =
            "HTTP request finished";

    public static final String HTTP_REQUEST_FAILED =
            "HTTP request failed";
}
