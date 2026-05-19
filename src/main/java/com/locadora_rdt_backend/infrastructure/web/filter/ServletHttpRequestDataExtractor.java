package com.locadora_rdt_backend.infrastructure.web.filter;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ServletHttpRequestDataExtractor implements HttpRequestDataExtractor {

    private final RequestIdGenerator requestIdGenerator;
    private final ClientIpResolver clientIpResolver;
    private final AuthenticatedUsernameResolver usernameResolver;

    public ServletHttpRequestDataExtractor(
            RequestIdGenerator requestIdGenerator,
            ClientIpResolver clientIpResolver,
            AuthenticatedUsernameResolver usernameResolver
    ) {
        this.requestIdGenerator = requestIdGenerator;
        this.clientIpResolver = clientIpResolver;
        this.usernameResolver = usernameResolver;
    }

    @Override
    public HttpRequestData extract(HttpServletRequest request) {
        HttpRequestData data = new HttpRequestData();

        data.setCorrelationId(resolveOrGenerate(
                request.getHeader(WebFilterConstants.CORRELATION_ID_HEADER)
        ));

        data.setTraceId(resolveOrGenerate(
                request.getHeader(WebFilterConstants.TRACE_ID_HEADER)
        ));

        data.setUsername(usernameResolver.resolve());
        data.setMethod(request.getMethod());
        data.setPath(request.getRequestURI());
        data.setClientIp(clientIpResolver.resolve(request));
        data.setUserAgent(resolveUserAgent(request));

        return data;
    }

    private String resolveOrGenerate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return requestIdGenerator.generate();
        }

        return value.trim();
    }

    private String resolveUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null || userAgent.trim().isEmpty()) {
            return WebFilterConstants.UNKNOWN;
        }

        return userAgent;
    }
}