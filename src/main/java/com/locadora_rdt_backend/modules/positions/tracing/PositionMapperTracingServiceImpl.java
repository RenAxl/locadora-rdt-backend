package com.locadora_rdt_backend.modules.positions.tracing;

import com.locadora_rdt_backend.infrastructure.tracing.span.MapperSpanService;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class PositionMapperTracingServiceImpl implements PositionMapperTracingService {

    private final MapperSpanService mapperSpanService;

    public PositionMapperTracingServiceImpl(MapperSpanService mapperSpanService) {
        this.mapperSpanService = mapperSpanService;
    }

    @Override
    public <T> T traceToDTO(Supplier<T> supplier) {
        return mapperSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_MAPPER_TO_DTO,
                supplier
        );
    }

    @Override
    public <T> T traceToDetailsDTO(Supplier<T> supplier) {
        return mapperSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_MAPPER_TO_DETAILS_DTO,
                supplier
        );
    }

    @Override
    public <T> T traceToEntity(Supplier<T> supplier) {
        return mapperSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_MAPPER_TO_ENTITY,
                supplier
        );
    }

    @Override
    public void traceCopyToEntity(Runnable runnable) {
        mapperSpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_MAPPER_COPY_TO_ENTITY,
                runnable
        );
    }
}