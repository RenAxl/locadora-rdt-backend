package com.locadora_rdt_backend.modules.positions.tracing;

import com.locadora_rdt_backend.infrastructure.tracing.span.RepositorySpanService;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class PositionRepositoryTracingServiceImpl implements PositionRepositoryTracingService {

    private final RepositorySpanService repositorySpanService;

    public PositionRepositoryTracingServiceImpl(RepositorySpanService repositorySpanService) {
        this.repositorySpanService = repositorySpanService;
    }

    @Override
    public <T> T traceSearchByName(Supplier<T> supplier) {
        return repositorySpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_REPOSITORY_SEARCH_BY_NAME,
                supplier
        );
    }

    @Override
    public <T> T traceFindById(Supplier<T> supplier) {
        return repositorySpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_REPOSITORY_FIND_BY_ID,
                supplier
        );
    }

    @Override
    public <T> T traceSave(Supplier<T> supplier) {
        return repositorySpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_REPOSITORY_SAVE,
                supplier
        );
    }

    @Override
    public void traceDelete(Runnable runnable) {
        repositorySpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_REPOSITORY_DELETE,
                runnable
        );
    }

    @Override
    public void traceFlush(Runnable runnable) {
        repositorySpanService.trace(
                PositionTracingConstants.MODULE,
                PositionTracingConstants.RESOURCE,
                PositionTracingConstants.OPERATION_REPOSITORY_FLUSH,
                runnable
        );
    }
}