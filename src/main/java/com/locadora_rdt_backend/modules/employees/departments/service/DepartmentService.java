package com.locadora_rdt_backend.modules.employees.departments.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.departments.repository.DepartmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;


@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public Page<DepartmentDTO> findAllPaged(String name, PageRequest pageRequest) {
        Page<Department> list = departmentRepository.find(name, pageRequest);
        Page<DepartmentDTO> listDto = list.map(department -> new DepartmentDTO(department));

        return listDto;
    }

    @Transactional(readOnly = true)
    public DepartmentDTO findById(Long id) {
        Department entity = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado"));
        return new DepartmentDTO(entity);
    }

    @Transactional
    public DepartmentDTO insert(DepartmentDTO dto) {
        Department entity = new Department();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(Instant.now());
        entity = departmentRepository.save(entity);
        return new DepartmentDTO(entity);
    }

    @Transactional
    public DepartmentDTO update(Long id, DepartmentDTO dto) {
        try {
            Department entity = departmentRepository.getOne(id);
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setUpdatedAt(Instant.now());
            entity = departmentRepository.save(entity);

            return new DepartmentDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            departmentRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }


}