package com.locadora_rdt_backend.modules.positions.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.positions.constants.PositionErrorMessages;
import com.locadora_rdt_backend.modules.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.positions.mapper.PositionMapper;
import com.locadora_rdt_backend.modules.positions.model.Position;
import com.locadora_rdt_backend.modules.positions.repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PositionServiceImpl implements PositionService {

    private static final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);

    private final PositionRepository repository;
    private final PositionMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public PositionServiceImpl(
            PositionRepository repository,
            PositionMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PositionDTO> findAllPaged(String name, PageRequest pageRequest) {
        String normalizedName = normalizeName(name);

        log.info(
                "POSITION_FIND_ALL_STARTED name={} page={} size={}",
                normalizedName,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );

        Page<PositionDTO> result = repository
                .searchByName(normalizedName, pageRequest)
                .map(mapper::toDTO);

        log.info(
                "POSITION_FIND_ALL_SUCCESS name={} totalElements={} totalPages={}",
                normalizedName,
                result.getTotalElements(),
                result.getTotalPages()
        );

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PositionDetailsDTO findById(Long id) {
        log.info("POSITION_FIND_BY_ID_STARTED positionId={}", id);

        Position entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("POSITION_FIND_BY_ID_NOT_FOUND positionId={}", id);

                    return new ResourceNotFoundException(
                            PositionErrorMessages.POSITION_NOT_FOUND
                    );
                });

        log.info(
                "POSITION_FIND_BY_ID_SUCCESS positionId={} positionName={}",
                entity.getId(),
                entity.getName()
        );

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Position findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PositionErrorMessages.POSITION_NOT_FOUND
                ));
    }

    @Override
    @Transactional
    public PositionDTO insert(PositionInsertDTO dto) {
        log.info("POSITION_CREATE_STARTED positionName={}", dto.getName());

        Position entity = mapper.toEntity(dto);

        entity.setCreatedBy(
                authenticationFacade.getAuthenticatedUsername()
        );

        Position savedEntity = repository.save(entity);

        log.info(
                "POSITION_CREATE_SUCCESS positionId={} positionName={}",
                savedEntity.getId(),
                savedEntity.getName()
        );

        return mapper.toDTO(savedEntity);
    }

    @Override
    @Transactional
    public PositionDTO update(Long id, PositionUpdateDTO dto) {
        log.info(
                "POSITION_UPDATE_STARTED positionId={} newPositionName={}",
                id,
                dto.getName()
        );

        Position entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("POSITION_UPDATE_NOT_FOUND positionId={}", id);

                    return new ResourceNotFoundException(
                            PositionErrorMessages.POSITION_NOT_FOUND
                    );
                });

        mapper.copyToEntity(dto, entity);

        entity.setUpdatedBy(
                authenticationFacade.getAuthenticatedUsername()
        );

        Position savedEntity = repository.save(entity);

        log.info(
                "POSITION_UPDATE_SUCCESS positionId={} positionName={}",
                savedEntity.getId(),
                savedEntity.getName()
        );

        return mapper.toDTO(savedEntity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("POSITION_DELETE_STARTED positionId={}", id);

        Position entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("POSITION_DELETE_NOT_FOUND positionId={}", id);

                    return new ResourceNotFoundException(
                            PositionErrorMessages.POSITION_NOT_FOUND
                    );
                });

        try {
            repository.delete(entity);
            repository.flush();

            log.info("POSITION_DELETE_SUCCESS positionId={}", id);

        } catch (DataIntegrityViolationException e) {
            log.error(
                    "POSITION_DELETE_FAILED_DATABASE_INTEGRITY positionId={}",
                    id,
                    e
            );

            throw new DatabaseException(
                    PositionErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
