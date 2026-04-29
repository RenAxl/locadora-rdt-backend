package com.locadora_rdt_backend.modules.employees.dto;

import com.locadora_rdt_backend.modules.employees.validation.EmployeeInsertValid;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@EmployeeInsertValid
public class EmployeeInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
    private String name;

    @NotBlank(message = "Matrícula é obrigatória")
    @Size(min = 3, max = 30, message = "Matrícula deve ter entre 3 e 30 caracteres")
    private String employeeCode;

    @Email(message = "Email inválido")
    @Size(max = 120, message = "Email deve ter no máximo 120 caracteres")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String phone;

    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    private String address;

    @DecimalMin(value = "0.0", inclusive = false, message = "Salário deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Salário inválido")
    private BigDecimal salary;

    @NotNull(message = "Data de admissão é obrigatória")
    private LocalDate hireDate;

    private LocalDate terminationDate;

    @NotBlank(message = "Tipo de contratação é obrigatório")
    @Size(max = 30, message = "Tipo de contratação deve ter no máximo 30 caracteres")
    private String employmentType;

    private Boolean active;

    @NotNull(message = "Cargo é obrigatório")
    private Long positionId;

    @NotNull(message = "Setor é obrigatório")
    private Long departmentId;

    public EmployeeInsertDTO() {
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

    public Long getPositionId() {
        return positionId;
    }

    public Long getDepartmentId() {
        return departmentId;
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

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}