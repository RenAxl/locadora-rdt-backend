package com.locadora_rdt_backend.modules.financial.payment.frequencies.dto;

import java.io.Serializable;
import java.time.Instant;

public class PaymentFrequencyDetailsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String frequency;
    private Integer days;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    public PaymentFrequencyDetailsDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getFrequency() {
        return frequency;
    }

    public Integer getDays() {
        return days;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setDays(Integer days) {
        this.days = days;
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
}
