package com.locadora_rdt_backend.modules.employees.mapper;

import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDetailsDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDTO toDTO(Employee entity) {
        EmployeeDTO dto = new EmployeeDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setSalary(entity.getSalary());
        dto.setHireDate(entity.getHireDate());
        dto.setEmploymentType(entity.getEmploymentType());
        dto.setActive(entity.getActive());
        dto.setPhotoContentType(entity.getPhotoContentType());

        if (entity.getPosition() != null) {
            dto.setPosition(new PositionDTO(
                    entity.getPosition().getId(),
                    entity.getPosition().getName()
            ));
        }

        if (entity.getDepartment() != null) {
            dto.setDepartment(new DepartmentDTO(
                    entity.getDepartment().getId(),
                    entity.getDepartment().getName()
            ));
        }

        return dto;
    }

    public EmployeeDetailsDTO toDetailsDTO(Employee entity) {
        EmployeeDetailsDTO dto = new EmployeeDetailsDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setAddress(entity.getAddress());
        dto.setSalary(entity.getSalary());
        dto.setHireDate(entity.getHireDate());
        dto.setTerminationDate(entity.getTerminationDate());
        dto.setEmploymentType(entity.getEmploymentType());
        dto.setActive(entity.getActive());
        dto.setPhotoContentType(entity.getPhotoContentType());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        if (entity.getPosition() != null) {
            dto.setPosition(new PositionDTO(
                    entity.getPosition().getId(),
                    entity.getPosition().getName()
            ));
        }

        if (entity.getDepartment() != null) {
            dto.setDepartment(new DepartmentDTO(
                    entity.getDepartment().getId(),
                    entity.getDepartment().getName()
            ));
        }

        return dto;
    }

    public Employee toEntity(EmployeeInsertDTO dto) {
        Employee entity = new Employee();

        entity.setName(dto.getName());
        entity.setEmployeeCode(dto.getEmployeeCode());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setSalary(dto.getSalary());
        entity.setHireDate(dto.getHireDate());
        entity.setTerminationDate(dto.getTerminationDate());
        entity.setEmploymentType(dto.getEmploymentType());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);

        return entity;
    }

    public void updateEntity(Employee entity, EmployeeUpdateDTO dto) {
        entity.setName(dto.getName());
        entity.setEmployeeCode(dto.getEmployeeCode());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setSalary(dto.getSalary());
        entity.setHireDate(dto.getHireDate());
        entity.setTerminationDate(dto.getTerminationDate());
        entity.setEmploymentType(dto.getEmploymentType());

        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}