package com.locadora_rdt_backend.modules.positions.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.metrics.model.MetricOperation;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.positions.constants.PositionErrorMessages;
import com.locadora_rdt_backend.modules.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.positions.logging.PositionLogger;
import com.locadora_rdt_backend.modules.positions.mapper.PositionMapper;
import com.locadora_rdt_backend.modules.positions.metrics.PositionMetricsService;
import com.locadora_rdt_backend.modules.positions.model.Position;
import com.locadora_rdt_backend.modules.positions.repository.PositionRepository;
import com.locadora_rdt_backend.modules.positions.tracing.PositionTracingService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository repository;
    private final PositionMapper mapper;
    private final AuthenticationFacade authenticationFacade;
    private final PositionLogger positionLogger;
    private final PositionMetricsService positionMetricsService;
    private final PositionTracingService positionTracingService;

    public PositionServiceImpl(
            PositionRepository repository,
            PositionMapper mapper,
            AuthenticationFacade authenticationFacade,
            PositionLogger positionLogger,
            PositionMetricsService positionMetricsService,
            PositionTracingService positionTracingService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
        this.positionLogger = positionLogger;
        this.positionMetricsService = positionMetricsService;
        this.positionTracingService = positionTracingService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PositionDTO> findAllPaged(
            String name,
            PageRequest pageRequest
    ) {
        return positionTracingService.traceFindAll(() ->
                positionMetricsService.recordExecutionTime(
                        MetricOperation.FIND_ALL,
                        () -> {
                            String normalizedName = normalizeName(name);

                            positionLogger.logSearchStarted(normalizedName);

                            Page<PositionDTO> result = repository
                                    .searchByName(normalizedName, pageRequest)
                                    .map(mapper::toDTO);

                            positionLogger.logSearchFinished(normalizedName);

                            return result;
                        }
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PositionDetailsDTO findById(Long id) {
        return positionTracingService.traceFindById(() ->
                positionMetricsService.recordExecutionTime(
                        MetricOperation.FIND_BY_ID,
                        () -> {
                            positionLogger.logDetailsStarted(id);

                            Position entity = repository.findById(id)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException(
                                                    PositionErrorMessages.POSITION_NOT_FOUND
                                            )
                                    );

                            positionLogger.logDetailsFinished(id);

                            return mapper.toDetailsDTO(entity);
                        }
                )
        );
    }

    @Override
    @Transactional
    public PositionDTO insert(PositionInsertDTO dto) {
        return positionTracingService.traceCreate(() ->
                positionMetricsService.recordExecutionTime(
                        MetricOperation.CREATE,
                        () -> {
                            Position entity = mapper.toEntity(dto);

                            entity.setCreatedBy(
                                    authenticationFacade.getAuthenticatedUsername()
                            );

                            entity = repository.save(entity);

                            positionLogger.logCreated(
                                    entity.getId(),
                                    entity.getName()
                            );

                            return mapper.toDTO(entity);
                        }
                )
        );
    }

    @Override
    @Transactional
    public PositionDTO update(
            Long id,
            PositionUpdateDTO dto
    ) {
        return positionTracingService.traceUpdate(() ->
                positionMetricsService.recordExecutionTime(
                        MetricOperation.UPDATE,
                        () -> {
                            Position entity = repository.findById(id)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException(
                                                    PositionErrorMessages.POSITION_NOT_FOUND
                                            )
                                    );

                            mapper.copyToEntity(dto, entity);

                            entity.setUpdatedBy(
                                    authenticationFacade.getAuthenticatedUsername()
                            );

                            entity = repository.save(entity);

                            positionLogger.logUpdated(
                                    entity.getId(),
                                    entity.getName()
                            );

                            return mapper.toDTO(entity);
                        }
                )
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        positionTracingService.traceDelete(() ->
                positionMetricsService.recordExecutionTime(
                        MetricOperation.DELETE,
                        () -> {
                            Position entity = repository.findById(id)
                                    .orElseThrow(() ->
                                            new ResourceNotFoundException(
                                                    PositionErrorMessages.POSITION_NOT_FOUND
                                            )
                                    );

                            try {
                                repository.delete(entity);
                                repository.flush();

                                positionLogger.logDeleted(id);

                            } catch (DataIntegrityViolationException e) {
                                positionLogger.logDeleteFailed(id, e);

                                throw new DatabaseException(
                                        PositionErrorMessages.DATABASE_INTEGRITY_VIOLATION
                                );
                            }
                        }
                )
        );
    }

    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }

        return name.trim();
    }
}