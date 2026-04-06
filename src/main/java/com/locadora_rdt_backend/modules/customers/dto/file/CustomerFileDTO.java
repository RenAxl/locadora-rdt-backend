package com.locadora_rdt_backend.modules.customers.dto.file;

import com.locadora_rdt_backend.modules.customers.model.CustomerFile;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CustomerFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long customerId;

    public CustomerFileDTO() {
    }

    public CustomerFileDTO(CustomerFile entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.originalFileName = entity.getOriginalFileName();
        this.storedFileName = entity.getStoredFileName();
        this.contentType = entity.getContentType();
        this.size = entity.getSize();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        this.customerId = entity.getCustomer().getId();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}