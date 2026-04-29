package com.locadora_rdt_backend.modules.employees.dto;

import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class EmployeeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String employeeCode;
    private String email;
    private String phone;
    private String address;
    private BigDecimal salary;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private String employmentType;
    private Boolean active;

    private String photoContentType;

    private Instant createdAt;
    private Instant updatedAt;

    private Long createdBy;
    private Long updatedBy;

    private PositionDTO position;
    private DepartmentDTO department;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Long id, String name, String employeeCode, String email, String phone, String address,
                       BigDecimal salary, LocalDate hireDate, LocalDate terminationDate, String employmentType,
                       Boolean active, String photoContentType, Instant createdAt, Instant updatedAt, Long createdBy,
                       Long updatedBy, PositionDTO position, DepartmentDTO department) {
        this.id = id;
        this.name = name;
        this.employeeCode = employeeCode;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.salary = salary;
        this.hireDate = hireDate;
        this.terminationDate = terminationDate;
        this.employmentType = employmentType;
        this.active = active;
        this.photoContentType = photoContentType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.position = position;
        this.department = department;
    }

    public EmployeeDTO(Employee entity) {
        id = entity.getId();
        name = entity.getName();
        employeeCode = entity.getEmployeeCode();
        email = entity.getEmail();
        phone = entity.getPhone();
        address = entity.getAddress();
        salary = entity.getSalary();
        hireDate = entity.getHireDate();
        terminationDate = entity.getTerminationDate();
        employmentType = entity.getEmploymentType();
        active = entity.getActive();
        photoContentType = entity.getPhotoContentType();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();
        createdBy = entity.getCreatedBy();
        updatedBy = entity.getUpdatedBy();

        if (entity.getPosition() != null) {
            position = new PositionDTO(entity.getPosition());
        }

        if (entity.getDepartment() != null) {
            department = new DepartmentDTO(entity.getDepartment());
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public Boolean getActive() {
        return active;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public PositionDTO getPosition() {
        return position;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }
}