package com.locadora_rdt_backend.infrastructure.web.filter;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class DefaultClientIpResolver implements ClientIpResolver {

    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    @Override
    public String resolve(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String value = request.getHeader(header);

            if (isValid(value)) {
                return extractFirstIp(value);
            }
        }

        return request.getRemoteAddr();
    }

    private boolean isValid(String value) {
        return value != null
                && !value.trim().isEmpty()
                && !"unknown".equalsIgnoreCase(value);
    }

    private String extractFirstIp(String value) {
        if (value.contains(",")) {
            return value.split(",")[0].trim();
        }

        return value.trim();
    }
}