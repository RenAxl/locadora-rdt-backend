package com.locadora_rdt_backend.infrastructure.web.filter;

import com.locadora_rdt_backend.shared.context.RequestContext;
import com.locadora_rdt_backend.shared.context.RequestContextFactory;
import com.locadora_rdt_backend.shared.context.RequestContextProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestObservabilityFilter extends OncePerRequestFilter {

    private final HttpRequestDataExtractor requestDataExtractor;
    private final RequestContextFactory requestContextFactory;
    private final RequestContextProvider requestContextProvider;

    public RequestObservabilityFilter(
            HttpRequestDataExtractor requestDataExtractor,
            RequestContextFactory requestContextFactory,
            RequestContextProvider requestContextProvider
    ) {
        this.requestDataExtractor = requestDataExtractor;
        this.requestContextFactory = requestContextFactory;
        this.requestContextProvider = requestContextProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        RequestContext context = null;

        try {
            HttpRequestData data = requestDataExtractor.extract(request);

            context = requestContextFactory.create(
                    data.getCorrelationId(),
                    data.getTraceId(),
                    data.getUsername(),
                    data.getMethod(),
                    data.getPath(),
                    data.getClientIp(),
                    data.getUserAgent()
            );

            requestContextProvider.set(context);

            response.setHeader(WebFilterConstants.CORRELATION_ID_HEADER, context.getCorrelationId());
            response.setHeader(WebFilterConstants.TRACE_ID_HEADER, context.getTraceId());

            filterChain.doFilter(request, response);

        } finally {
            if (context != null) {
                requestContextFactory.finish(context);
            }

            requestContextProvider.clear();
        }
    }
}