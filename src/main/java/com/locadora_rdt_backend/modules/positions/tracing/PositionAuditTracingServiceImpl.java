package com.locadora_rdt_backend.modules.positions.tracing;

import com.locadora_rdt_backend.infrastructure.tracing.span.AuditSpanService;
import org.springframework.stereotype.Service;

@Service
public class PositionAuditTracingServiceImpl implements PositionAuditTracingService {

    private final AuditSpanService auditSpanService;

    public PositionAuditTracingServiceImpl(AuditSpanService auditSpanService) {
        this.auditSpanService = auditSpanService;
    }

    @Override
    public void traceAuditCreate(Runnable runnable) {
        auditSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_AUDIT_CREATE,
                runnable
        );
    }

    @Override
    public void traceAuditUpdate(Runnable runnable) {
        auditSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_AUDIT_UPDATE,
                runnable
        );
    }

    @Override
    public void traceAuditDelete(Runnable runnable) {
        auditSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_AUDIT_DELETE,
                runnable
        );
    }
}