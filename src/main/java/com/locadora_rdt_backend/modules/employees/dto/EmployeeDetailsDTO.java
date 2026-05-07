package com.locadora_rdt_backend.modules.employees.dto;

import com.locadora_rdt_backend.modules.employees.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class EmployeeDetailsDTO implements Serializable {
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
    private String createdBy;
    private String updatedBy;
    private PositionDTO position;
    private DepartmentDTO department;

    public EmployeeDetailsDTO() {}

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

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
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

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setPosition(PositionDTO position) {
        this.position = position;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

}