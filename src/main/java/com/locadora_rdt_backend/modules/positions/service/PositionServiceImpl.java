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

        return repository.searchByName(normalizedName, pageRequest).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PositionDetailsDTO findById(Long id) {
        Position entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                PositionErrorMessages.POSITION_NOT_FOUND
                        )
                );

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional
    public PositionDTO insert(PositionInsertDTO dto) {
        Position entity = mapper.toEntity(dto);

        entity.setCreatedBy(
                authenticationFacade.getAuthenticatedUsername()
        );

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public PositionDTO update(
            Long id,
            PositionUpdateDTO dto
    ) {
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

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Position entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                PositionErrorMessages.POSITION_NOT_FOUND
                        )
                );
        try {
            repository.delete(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    PositionErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }

        return name.trim();
    }
}