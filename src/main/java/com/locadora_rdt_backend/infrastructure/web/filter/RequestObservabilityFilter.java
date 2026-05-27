package com.locadora_rdt_backend.infrastructure.web.filter;

import com.locadora_rdt_backend.infrastructure.web.filter.logging.HttpRequestLogger;
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
    private final HttpRequestLogger httpRequestLogger;

    public RequestObservabilityFilter(
            HttpRequestDataExtractor requestDataExtractor,
            RequestContextFactory requestContextFactory,
            RequestContextProvider requestContextProvider,
            HttpRequestLogger httpRequestLogger
    ) {
        this.requestDataExtractor = requestDataExtractor;
        this.requestContextFactory = requestContextFactory;
        this.requestContextProvider = requestContextProvider;
        this.httpRequestLogger = httpRequestLogger;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        HttpRequestData requestData =
                requestDataExtractor.extract(request);

        RequestContext context =
                requestContextFactory.create(
                        requestData.getCorrelationId(),
                        requestData.getTraceId(),
                        requestData.getUsername(),
                        requestData.getMethod(),
                        requestData.getPath(),
                        requestData.getClientIp(),
                        requestData.getUserAgent()
                );

        requestContextProvider.set(context);

        httpRequestLogger.logRequestStarted(
                request.getMethod(),
                request.getRequestURI()
        );

        try {
            filterChain.doFilter(request, response);

            requestContextFactory.finish(context);

            httpRequestLogger.logRequestFinished(
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    context.getDurationMs()
            );

        } catch (Exception exception) {
            requestContextFactory.finish(context);

            httpRequestLogger.logRequestFailed(
                    request.getMethod(),
                    request.getRequestURI(),
                    exception
            );

            throw exception;

        } finally {
            requestContextProvider.clear();
        }
    }
}