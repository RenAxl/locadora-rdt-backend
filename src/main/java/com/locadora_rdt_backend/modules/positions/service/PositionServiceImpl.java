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
import com.locadora_rdt_backend.modules.positions.tracing.PositionMapperTracingService;
import com.locadora_rdt_backend.modules.positions.tracing.PositionMetricsTracingService;
import com.locadora_rdt_backend.modules.positions.tracing.PositionRepositoryTracingService;
import com.locadora_rdt_backend.modules.positions.tracing.PositionTracingService;
import com.locadora_rdt_backend.modules.positions.tracing.PositionValidationTracingService;
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
    private final PositionRepositoryTracingService positionRepositoryTracingService;
    private final PositionMapperTracingService positionMapperTracingService;
    private final PositionValidationTracingService positionValidationTracingService;
    private final PositionMetricsTracingService positionMetricsTracingService;

    public PositionServiceImpl(
            PositionRepository repository,
            PositionMapper mapper,
            AuthenticationFacade authenticationFacade,
            PositionLogger positionLogger,
            PositionMetricsService positionMetricsService,
            PositionTracingService positionTracingService,
            PositionRepositoryTracingService positionRepositoryTracingService,
            PositionMapperTracingService positionMapperTracingService,
            PositionValidationTracingService positionValidationTracingService,
            PositionMetricsTracingService positionMetricsTracingService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
        this.positionLogger = positionLogger;
        this.positionMetricsService = positionMetricsService;
        this.positionTracingService = positionTracingService;
        this.positionRepositoryTracingService = positionRepositoryTracingService;
        this.positionMapperTracingService = positionMapperTracingService;
        this.positionValidationTracingService = positionValidationTracingService;
        this.positionMetricsTracingService = positionMetricsTracingService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PositionDTO> findAllPaged(
            String name,
            PageRequest pageRequest
    ) {
        return positionTracingService.traceFindAll(() ->
                positionMetricsTracingService.traceMetricsRecord(() ->
                        positionMetricsService.recordExecutionTime(
                                MetricOperation.FIND_ALL,
                                () -> {
                                    String normalizedName = normalizeName(name);

                                    positionLogger.logSearchStarted(normalizedName);

                                    Page<PositionDTO> result = positionRepositoryTracingService
                                            .traceSearchByName(() ->
                                                    repository.searchByName(normalizedName, pageRequest)
                                            )
                                            .map(position -> positionMapperTracingService
                                                    .traceToDTO(() -> mapper.toDTO(position))
                                            );

                                    positionLogger.logSearchFinished(normalizedName);

                                    return result;
                                }
                        )
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PositionDetailsDTO findById(Long id) {
        return positionTracingService.traceFindById(() ->
                positionMetricsTracingService.traceMetricsRecord(() ->
                        positionMetricsService.recordExecutionTime(
                                MetricOperation.FIND_BY_ID,
                                () -> {
                                    positionLogger.logDetailsStarted(id);

                                    Position entity = positionRepositoryTracingService.traceFindById(() ->
                                            repository.findById(id)
                                                    .orElseThrow(() ->
                                                            new ResourceNotFoundException(
                                                                    PositionErrorMessages.POSITION_NOT_FOUND
                                                            )
                                                    )
                                    );

                                    PositionDetailsDTO dto = positionMapperTracingService
                                            .traceToDetailsDTO(() -> mapper.toDetailsDTO(entity));

                                    positionLogger.logDetailsFinished(id);

                                    return dto;
                                }
                        )
                )
        );
    }

    @Override
    @Transactional
    public PositionDTO insert(PositionInsertDTO dto) {
        return positionTracingService.traceCreate(() ->
                positionMetricsTracingService.traceMetricsRecord(() ->
                        positionMetricsService.recordExecutionTime(
                                MetricOperation.CREATE,
                                () -> {
                                    positionValidationTracingService.traceValidateCreate(() -> {
                                        // Espaço reservado para validações de criação.
                                    });

                                    Position entity = positionMapperTracingService.traceToEntity(() ->
                                            mapper.toEntity(dto)
                                    );

                                    entity.setCreatedBy(
                                            authenticationFacade.getAuthenticatedUsername()
                                    );

                                    Position savedEntity = positionRepositoryTracingService.traceSave(() ->
                                            repository.save(entity)
                                    );

                                    positionLogger.logCreated(
                                            savedEntity.getId(),
                                            savedEntity.getName()
                                    );

                                    return positionMapperTracingService.traceToDTO(() ->
                                            mapper.toDTO(savedEntity)
                                    );
                                }
                        )
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
                positionMetricsTracingService.traceMetricsRecord(() ->
                        positionMetricsService.recordExecutionTime(
                                MetricOperation.UPDATE,
                                () -> {
                                    positionValidationTracingService.traceValidateUpdate(() -> {
                                        // Espaço reservado para validações de atualização.
                                    });

                                    Position entity = positionRepositoryTracingService.traceFindById(() ->
                                            repository.findById(id)
                                                    .orElseThrow(() ->
                                                            new ResourceNotFoundException(
                                                                    PositionErrorMessages.POSITION_NOT_FOUND
                                                            )
                                                    )
                                    );

                                    positionMapperTracingService.traceCopyToEntity(() ->
                                            mapper.copyToEntity(dto, entity)
                                    );

                                    entity.setUpdatedBy(
                                            authenticationFacade.getAuthenticatedUsername()
                                    );

                                    Position savedEntity = positionRepositoryTracingService.traceSave(() ->
                                            repository.save(entity)
                                    );

                                    positionLogger.logUpdated(
                                            savedEntity.getId(),
                                            savedEntity.getName()
                                    );

                                    return positionMapperTracingService.traceToDTO(() ->
                                            mapper.toDTO(savedEntity)
                                    );
                                }
                        )
                )
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        positionTracingService.traceDelete(() ->
                positionMetricsTracingService.traceMetricsRecord(() ->
                        positionMetricsService.recordExecutionTime(
                                MetricOperation.DELETE,
                                () -> {
                                    Position entity = positionRepositoryTracingService.traceFindById(() ->
                                            repository.findById(id)
                                                    .orElseThrow(() ->
                                                            new ResourceNotFoundException(
                                                                    PositionErrorMessages.POSITION_NOT_FOUND
                                                            )
                                                    )
                                    );

                                    positionValidationTracingService.traceValidateDelete(() -> {
                                        // Espaço reservado para validações de exclusão.
                                    });

                                    try {
                                        positionRepositoryTracingService.traceDelete(() ->
                                                repository.delete(entity)
                                        );

                                        positionRepositoryTracingService.traceFlush(repository::flush);

                                        positionLogger.logDeleted(id);

                                    } catch (DataIntegrityViolationException e) {
                                        positionLogger.logDeleteFailed(id, e);

                                        throw new DatabaseException(
                                                PositionErrorMessages.DATABASE_INTEGRITY_VIOLATION
                                        );
                                    }
                                }
                        )
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