package com.locadora_rdt_backend.tests.factories;

import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeDetailsDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class EmployeeFactory {

    public static Employee createEmployee() {

        Employee employee = new Employee();

        employee.setId(1L);
        employee.setName("João da Silva");
        employee.setEmployeeCode("EMP-001");
        employee.setEmail("joao@locadora.com");
        employee.setPhone("(31)99999-9999");
        employee.setAddress("Rua A, 100");

        employee.setSalary(new BigDecimal("4500.00"));

        employee.setHireDate(LocalDate.of(2024, 1, 10));
        employee.setTerminationDate(null);

        employee.setEmploymentType("CLT");
        employee.setActive(true);

        employee.setPhoto("foto-teste".getBytes());
        employee.setPhotoContentType("image/png");

        employee.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        employee.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));

        employee.setCreatedBy("admin");
        employee.setUpdatedBy("admin");

        employee.setPosition(createPosition());
        employee.setDepartment(createDepartment());

        return employee;
    }

    public static EmployeeDTO createEmployeeDTO() {

        EmployeeDTO dto = new EmployeeDTO();

        dto.setId(1L);
        dto.setName("João da Silva");
        dto.setEmployeeCode("EMP-001");
        dto.setEmail("joao@locadora.com");
        dto.setPhone("(31)99999-9999");

        dto.setSalary(new BigDecimal("4500.00"));

        dto.setHireDate(LocalDate.of(2024, 1, 10));

        dto.setEmploymentType("CLT");
        dto.setActive(true);

        dto.setPhotoContentType("image/png");

        dto.setPosition(createPositionDTO());
        dto.setDepartment(createDepartmentDTO());

        return dto;
    }

    public static EmployeeDetailsDTO createEmployeeDetailsDTO() {

        EmployeeDetailsDTO dto = new EmployeeDetailsDTO();

        dto.setId(1L);
        dto.setName("João da Silva");
        dto.setEmployeeCode("EMP-001");
        dto.setEmail("joao@locadora.com");
        dto.setPhone("(31)99999-9999");
        dto.setAddress("Rua A, 100");

        dto.setSalary(new BigDecimal("4500.00"));

        dto.setHireDate(LocalDate.of(2024, 1, 10));
        dto.setTerminationDate(null);

        dto.setEmploymentType("CLT");
        dto.setActive(true);

        dto.setPhotoContentType("image/png");

        dto.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        dto.setUpdatedAt(Instant.parse("2026-01-02T10:00:00Z"));

        dto.setCreatedBy("admin");
        dto.setUpdatedBy("admin");

        dto.setPosition(createPositionDTO());
        dto.setDepartment(createDepartmentDTO());

        return dto;
    }

    public static EmployeeInsertDTO createEmployeeInsertDTO() {

        EmployeeInsertDTO dto = new EmployeeInsertDTO();

        dto.setName("João da Silva");
        dto.setEmployeeCode("EMP-001");
        dto.setEmail("joao@locadora.com");
        dto.setPhone("(31)99999-9999");
        dto.setAddress("Rua A, 100");

        dto.setSalary(new BigDecimal("4500.00"));

        dto.setHireDate(LocalDate.of(2024, 1, 10));

        dto.setEmploymentType("CLT");
        dto.setActive(true);

        dto.setPositionId(1L);
        dto.setDepartmentId(1L);

        return dto;
    }

    public static EmployeeUpdateDTO createEmployeeUpdateDTO() {

        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();

        dto.setId(1L);

        dto.setName("João da Silva Atualizado");
        dto.setEmployeeCode("EMP-001");
        dto.setEmail("joao.atualizado@locadora.com");
        dto.setPhone("(31)98888-8888");
        dto.setAddress("Rua B, 200");

        dto.setSalary(new BigDecimal("5500.00"));

        dto.setHireDate(LocalDate.of(2024, 1, 10));

        dto.setEmploymentType("PJ");
        dto.setActive(true);

        dto.setPositionId(1L);
        dto.setDepartmentId(1L);

        return dto;
    }

    public static Position createPosition() {

        Position position = new Position();

        position.setId(1L);
        position.setName("Desenvolvedor Backend");

        return position;
    }

    public static PositionDTO createPositionDTO() {

        PositionDTO dto = new PositionDTO();

        dto.setId(1L);
        dto.setName("Desenvolvedor Backend");

        return dto;
    }

    public static Department createDepartment() {

        Department department = new Department();

        department.setId(1L);
        department.setName("Tecnologia");

        return department;
    }

    public static DepartmentDTO createDepartmentDTO() {

        DepartmentDTO dto = new DepartmentDTO();

        dto.setId(1L);
        dto.setName("Tecnologia");

        return dto;
    }
}