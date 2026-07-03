package com.locadora_rdt_backend.modules.financial.payment.settings.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class FinancialSettingDetailsDTO extends FinancialSettingDTO {
    private static final long serialVersionUID = 1L;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    public FinancialSettingDetailsDTO() {
        // Required by frameworks and serializers.
    }

    public FinancialSettingDetailsDTO(
            Long id,
            BigDecimal defaultLateFeePercent,
            BigDecimal defaultLateInterestPercent
    ) {
        super(id, defaultLateFeePercent, defaultLateInterestPercent);
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
