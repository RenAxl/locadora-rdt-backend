package com.locadora_rdt_backend.infrastructure.web.filter;

import javax.servlet.http.HttpServletRequest;

public interface HttpRequestDataExtractor {

    HttpRequestData extract(HttpServletRequest request);
}
