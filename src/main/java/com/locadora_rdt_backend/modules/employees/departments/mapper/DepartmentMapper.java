package com.locadora_rdt_backend.modules.employees.departments.mapper;

import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentDTO toDTO(Department entity) {
        return new DepartmentDTO(
                entity.getId(),
                entity.getName()
        );
    }

    public DepartmentDetailsDTO toDetailsDTO(Department entity) {
        DepartmentDetailsDTO dto = new DepartmentDetailsDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public Department toEntity(DepartmentInsertDTO dto) {
        Department entity = new Department();

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public void updateEntity(Department entity, DepartmentUpdateDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
    }
}