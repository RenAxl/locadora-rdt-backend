package com.locadora_rdt_backend.modules.financial.payables.dto;

import com.locadora_rdt_backend.modules.financial.payables.constants.PayableValidationConstants;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PayableSaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = PayableValidationConstants.DESCRIPTION_REQUIRED)
    @Size(
            min = PayableValidationConstants.DESCRIPTION_MIN_LENGTH,
            max = PayableValidationConstants.DESCRIPTION_MAX_LENGTH,
            message = PayableValidationConstants.DESCRIPTION_LENGTH
    )
    private String description;

    @NotNull(message = PayableValidationConstants.AMOUNT_REQUIRED)
    @DecimalMin(
            value = PayableValidationConstants.MINIMUM_AMOUNT,
            message = PayableValidationConstants.AMOUNT_MUST_BE_POSITIVE
    )
    private BigDecimal amount;

    @NotNull(message = PayableValidationConstants.DUE_DATE_REQUIRED)
    private LocalDate dueDate;
    private LocalDate paymentDate;

    private Long supplierId;
    private Long employeeId;
    private Long paymentMethodId;
    private Long paymentFrequencyId;
    private String note;
    private String fileName;

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public Long getPaymentFrequencyId() {
        return paymentFrequencyId;
    }

    public String getNote() {
        return note;
    }

    public String getFileName() {
        return fileName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public void setPaymentFrequencyId(Long paymentFrequencyId) {
        this.paymentFrequencyId = paymentFrequencyId;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
