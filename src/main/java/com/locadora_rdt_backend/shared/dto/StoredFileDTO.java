package com.locadora_rdt_backend.shared.dto;

import com.locadora_rdt_backend.shared.model.StoredFile;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class StoredFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected StoredFileDTO() {
        // Required by frameworks and serializers.
    }

    protected StoredFileDTO(StoredFile entity) {
        id = entity.getId();
        name = entity.getName();
        originalFileName = entity.getOriginalFileName();
        storedFileName = entity.getStoredFileName();
        contentType = entity.getContentType();
        size = entity.getSize();
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();
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
}
