package com.locadora_rdt_backend.modules.employees.dto;

import com.locadora_rdt_backend.modules.employees.model.Employee;

import javax.validation.constraints.*;
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
    private Boolean active = true;
    private byte[] photo;
    private String photoContentType;
    private Instant createdAt;
    private Instant updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long positionId;
    private String positionName;
    private Long departmentId;
    private String departmentName;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Long id, String name, String employeeCode, String email, String phone, String address,
                       BigDecimal salary, LocalDate hireDate, LocalDate terminationDate, String employmentType,
                       Boolean active, byte[] photo, String photoContentType, Instant createdAt, Instant updatedAt, Long createdBy,
                       Long updatedBy, Long positionId, String positionName, Long departmentId, String departmentName) {
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
        this.photo = photo;
        this.photoContentType = photoContentType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.positionId = positionId;
        this.positionName = positionName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public EmployeeDTO(Employee entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.employeeCode = entity.getEmployeeCode();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();
        this.address = entity.getAddress();
        this.salary = entity.getSalary();
        this.hireDate = entity.getHireDate();
        this.terminationDate = entity.getTerminationDate();
        this.employmentType = entity.getEmploymentType();
        this.active = entity.getActive();
        this.photo = entity.getPhoto();
        this.photoContentType = entity.getPhotoContentType();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        this.createdBy = entity.getCreatedBy();
        this.updatedBy = entity.getUpdatedBy();
        this.positionId = entity.getPosition().getId();
        this.positionName = entity.getPosition().getName();
        this.departmentId = entity.getDepartment().getId();
        this.departmentName = entity.getDepartment().getName();
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

    public byte[] getPhoto() {
        return photo;
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

    public Long getPositionId() {
        return positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
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

    public void setPhoto(byte[] photo) {
        this.photo = photo;
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

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}