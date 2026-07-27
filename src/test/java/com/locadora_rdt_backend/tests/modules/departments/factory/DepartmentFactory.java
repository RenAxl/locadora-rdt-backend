package com.locadora_rdt_backend.tests.modules.departments.factory;

import com.locadora_rdt_backend.modules.organization.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.organization.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.organization.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.organization.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.organization.departments.model.Department;

import java.time.Instant;

public class DepartmentFactory {

    public static Department createDepartment() {
        Department department = new Department(1L, "Tecnologia", "Departamento de tecnologia");
        department.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        department.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));
        department.setCreatedBy("admin");
        department.setUpdatedBy("admin");
        return department;
    }

    public static DepartmentDTO createDepartmentDTO() {
        return createDepartmentDTO(createDepartment());
    }

    public static DepartmentDTO createDepartmentDTO(Department department) {
        return new DepartmentDTO(department.getId(), department.getName());
    }

    public static DepartmentDetailsDTO createDepartmentDetailsDTO() {
        return createDepartmentDetailsDTO(createDepartment());
    }

    public static DepartmentDetailsDTO createDepartmentDetailsDTO(Department department) {
        DepartmentDetailsDTO dto = new DepartmentDetailsDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setCreatedAt(department.getCreatedAt());
        dto.setUpdatedAt(department.getUpdatedAt());
        dto.setCreatedBy(department.getCreatedBy());
        dto.setUpdatedBy(department.getUpdatedBy());
        return dto;
    }

    public static DepartmentInsertDTO createDepartmentInsertDTO() {
        DepartmentInsertDTO dto = new DepartmentInsertDTO();
        dto.setName("Tecnologia");
        dto.setDescription("Departamento de tecnologia");
        return dto;
    }

    public static DepartmentUpdateDTO createDepartmentUpdateDTO() {
        DepartmentUpdateDTO dto = new DepartmentUpdateDTO();
        dto.setName("Tecnologia Atualizada");
        dto.setDescription("Departamento atualizado");
        return dto;
    }
}
