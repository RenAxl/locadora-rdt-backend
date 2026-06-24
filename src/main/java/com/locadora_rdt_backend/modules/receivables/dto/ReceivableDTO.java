package com.locadora_rdt_backend.modules.receivables.dto;

import com.locadora_rdt_backend.modules.receivables.model.ReceivableStatus;

import java.time.Instant;

public class ReceivableDTO extends ReceivableBaseDTO {
    private static final long serialVersionUID = 1L;

    private Long id;
    private ReceivableStatus status;
    private Instant createdAt;
    private String createdBy;

    public ReceivableDTO() {
        // Required by frameworks and serializers.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReceivableStatus getStatus() {
        return status;
    }

    public void setStatus(ReceivableStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
