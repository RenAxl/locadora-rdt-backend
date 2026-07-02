package com.locadora_rdt_backend.modules.financial.receivables.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

public class ReceivableInstallmentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Informe a quantidade de parcelas")
    @Min(value = 2, message = "O parcelamento deve ter pelo menos duas parcelas")
    private Integer installments;

    private LocalDate firstDueDate;

    public Integer getInstallments() {
        return installments;
    }

    public LocalDate getFirstDueDate() {
        return firstDueDate;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public void setFirstDueDate(LocalDate firstDueDate) {
        this.firstDueDate = firstDueDate;
    }
}
