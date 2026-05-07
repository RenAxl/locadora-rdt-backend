package com.locadora_rdt_backend.modules.employees.departments.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.employees.departments.mapper.DepartmentMapper;
import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.departments.repository.DepartmentRepository;
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
public class DepartmentService {

    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;

    public DepartmentService(DepartmentRepository repository, DepartmentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<DepartmentDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(name, pageRequest)
                .map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public DepartmentDetailsDTO findById(Long id) {
        Department entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado"));

        return mapper.toDetailsDTO(entity);
    }

    @Transactional
    public DepartmentDTO insert(DepartmentInsertDTO dto) {
        Department entity = mapper.toEntity(dto);

        entity.setCreatedBy(getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Transactional
    public DepartmentDTO update(Long id, DepartmentUpdateDTO dto) {
        try {
            Department entity = repository.getOne(id);

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