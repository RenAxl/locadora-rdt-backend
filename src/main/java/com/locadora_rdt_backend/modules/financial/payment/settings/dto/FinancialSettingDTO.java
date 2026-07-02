package com.locadora_rdt_backend.modules.financial.payment.settings.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class FinancialSettingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private BigDecimal defaultLateFeePercent;
    private BigDecimal defaultLateInterestPercent;

    public FinancialSettingDTO() {
        // Required by frameworks and serializers.
    }

    public FinancialSettingDTO(
            Long id,
            BigDecimal defaultLateFeePercent,
            BigDecimal defaultLateInterestPercent
    ) {
        this.id = id;
        this.defaultLateFeePercent = defaultLateFeePercent;
        this.defaultLateInterestPercent = defaultLateInterestPercent;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getDefaultLateFeePercent() {
        return defaultLateFeePercent;
    }

    public BigDecimal getDefaultLateInterestPercent() {
        return defaultLateInterestPercent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDefaultLateFeePercent(BigDecimal defaultLateFeePercent) {
        this.defaultLateFeePercent = defaultLateFeePercent;
    }

    public void setDefaultLateInterestPercent(BigDecimal defaultLateInterestPercent) {
        this.defaultLateInterestPercent = defaultLateInterestPercent;
    }
}
