package com.locadora_rdt_backend.infrastructure.tracing.span;

import com.locadora_rdt_backend.infrastructure.tracing.model.SpanType;
import com.locadora_rdt_backend.infrastructure.tracing.service.ApplicationTracingService;
import org.springframework.stereotype.Service;

@Service
public class DefaultHttpSpanService extends AbstractSpanService implements HttpSpanService {

    public DefaultHttpSpanService(ApplicationTracingService applicationTracingService) {
        super(applicationTracingService);
    }

    @Override
    protected SpanType getSpanType() {
        return SpanType.HTTP;
    }
}
