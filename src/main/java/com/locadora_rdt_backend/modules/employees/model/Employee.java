package com.locadora_rdt_backend.modules.employees.model;

import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_employee")
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "employee_code", unique = true, nullable = false)
    private String employeeCode;

    @Column(unique = true)
    private String email;

    private String phone;
    private String address;
    private BigDecimal salary;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private String employmentType;

    @Column(nullable = false)
    private Boolean active = true;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "photo_data", columnDefinition = "BYTEA")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeFile> files = new ArrayList<>();

    public Employee() {
    }

    public Employee(Long id, String name, String employeeCode, String email, String phone,
                    String address, BigDecimal salary, LocalDate hireDate,
                    LocalDate terminationDate, String employmentType,
                    byte[] photo, String photoContentType, Boolean active,
                    Long createdBy, Long updatedBy, Position position, Department department) {
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
        this.photo = photo;
        this.photoContentType = photoContentType;
        this.active = active;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.position = position;
        this.department = department;
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
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

    public Position getPosition() {
        return position;
    }

    public Department getDepartment() {
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

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Employee other = (Employee) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }
}