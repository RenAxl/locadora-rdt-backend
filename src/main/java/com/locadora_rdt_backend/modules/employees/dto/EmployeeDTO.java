package com.locadora_rdt_backend.modules.employees.dto;

import com.locadora_rdt_backend.modules.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String employeeCode;
    private String email;
    private String phone;
    private BigDecimal salary;
    private LocalDate hireDate;
    private String employmentType;
    private Boolean active;
    private String photoContentType;
    private PositionDTO position;
    private DepartmentDTO department;

    public EmployeeDTO() {
        // Required by frameworks and serializers.
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

    public BigDecimal getSalary() {
        return salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
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

    public PositionDTO getPosition() {
        return position;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public void setPosition(PositionDTO position) {
        this.position = position;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }
}