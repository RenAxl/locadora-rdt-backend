package com.locadora_rdt_backend.modules.departments.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.departments.constants.DepartmentErrorMessages;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.departments.mapper.DepartmentMapper;
import com.locadora_rdt_backend.modules.departments.model.Department;
import com.locadora_rdt_backend.modules.departments.repository.DepartmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public DepartmentServiceImpl(
            DepartmentRepository repository,
            DepartmentMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentDTO> findAllPaged(String name, PageRequest pageRequest) {
        String normalizedName = name == null ? "" : name.trim();
        return repository.find(normalizedName, pageRequest).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDetailsDTO findById(Long id) {
        return mapper.toDetailsDTO(getEntityById(id));
    }

    @Override
    @Transactional
    public DepartmentDTO insert(DepartmentInsertDTO dto) {
        Department entity = mapper.toEntity(dto);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public DepartmentDTO update(Long id, DepartmentUpdateDTO dto) {
        Department entity = getEntityById(id);
        mapper.updateEntity(entity, dto);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department entity = getEntityById(id);
        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(DepartmentErrorMessages.DATABASE_INTEGRITY_VIOLATION);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Department findEntityById(Long id) {
        return getEntityById(id);
    }

    private Department getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        DepartmentErrorMessages.DEPARTMENT_NOT_FOUND
                ));
    }
}
