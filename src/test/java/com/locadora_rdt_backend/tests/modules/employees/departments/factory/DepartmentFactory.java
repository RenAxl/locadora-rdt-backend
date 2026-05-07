package com.locadora_rdt_backend.tests.modules.employees.departments.factory;

import com.locadora_rdt_backend.modules.employees.departments.dto.*;
import com.locadora_rdt_backend.modules.employees.departments.model.Department;

import java.time.Instant;

public class DepartmentFactory {

    public static Department createDepartment() {
        Department department = new Department();

        department.setId(1L);
        department.setName("Tecnologia");
        department.setDescription("Departamento de Tecnologia da Informação");
        department.setCreatedAt(Instant.now());
        department.setUpdatedAt(Instant.now());
        department.setCreatedBy("SYSTEM");
        department.setUpdatedBy("SYSTEM");

        return department;
    }

    public static Department createDepartment(Long id) {
        Department department = createDepartment();
        department.setId(id);
        return department;
    }

    public static DepartmentDTO createDepartmentDTO() {
        return new DepartmentDTO(1L, "TI");
    }

    public static DepartmentDTO createDepartmentDTO(Department entity) {
        return new DepartmentDTO(entity.getId(), entity.getName());
    }

    public static DepartmentDetailsDTO createDepartmentDetailsDTO() {
        Department entity = createDepartment();
        return createDepartmentDetailsDTO(entity);
    }

    public static DepartmentDetailsDTO createDepartmentDetailsDTO(Department entity) {
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

    public static DepartmentInsertDTO createDepartmentInsertDTO() {
        DepartmentInsertDTO dto = new DepartmentInsertDTO();

        dto.setName("Tecnologia");
        dto.setDescription("Departamento de Tecnologia da Informação");

        return dto;
    }

    public static DepartmentUpdateDTO createDepartmentUpdateDTO() {
        DepartmentUpdateDTO dto = new DepartmentUpdateDTO();

        dto.setName("Tecnologia Atualizado");
        dto.setDescription("Departamento atualizado");

        return dto;
    }
}
