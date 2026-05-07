package com.locadora_rdt_backend.modules.employees.positions.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.employees.positions.mapper.PositionMapper;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import com.locadora_rdt_backend.modules.employees.positions.repository.PositionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class PositionService {

    private final PositionRepository repository;
    private final PositionMapper mapper;

    public PositionService(PositionRepository repository, PositionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<PositionDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(name, pageRequest)
                .map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PositionDetailsDTO findById(Long id) {
        Position entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado"));

        return mapper.toDetailsDTO(entity);
    }

    @Transactional
    public PositionDTO insert(PositionInsertDTO dto) {
        Position entity = mapper.toEntity(dto);

        entity.setCreatedBy(getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Transactional
    public PositionDTO update(Long id, PositionUpdateDTO dto) {
        try {
            Position entity = repository.getOne(id);

            mapper.updateEntity(entity, dto);
            entity.setUpdatedBy(getAuthenticatedUsername());

            entity = repository.save(entity);

            return mapper.toDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }

        return authentication.getName();
    }
}