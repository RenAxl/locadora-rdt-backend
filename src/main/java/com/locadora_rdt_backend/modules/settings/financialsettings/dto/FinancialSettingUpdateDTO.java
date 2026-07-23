package com.locadora_rdt_backend.modules.settings.financialsettings.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

public class FinancialSettingUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Percentual padrão de multa por atraso é obrigatório")
    @DecimalMin(value = "0.0", message = "Percentual padrão de multa por atraso deve ser maior ou igual a zero")
    @Digits(integer = 10, fraction = 2, message = "Percentual padrão de multa por atraso inválido")
    private BigDecimal defaultLateFeePercent;

    @NotNull(message = "Percentual padrão de juros por atraso é obrigatório")
    @DecimalMin(value = "0.0", message = "Percentual padrão de juros por atraso deve ser maior ou igual a zero")
    @Digits(integer = 10, fraction = 2, message = "Percentual padrão de juros por atraso inválido")
    private BigDecimal defaultLateInterestPercent;

    public FinancialSettingUpdateDTO() {
        // Required by frameworks and serializers.
    }

    public BigDecimal getDefaultLateFeePercent() {
        return defaultLateFeePercent;
    }

    public BigDecimal getDefaultLateInterestPercent() {
        return defaultLateInterestPercent;
    }

    public void setDefaultLateFeePercent(BigDecimal defaultLateFeePercent) {
        this.defaultLateFeePercent = defaultLateFeePercent;
    }

    public void setDefaultLateInterestPercent(BigDecimal defaultLateInterestPercent) {
        this.defaultLateInterestPercent = defaultLateInterestPercent;
    }
}
