package com.locadora_rdt_backend.modules.receivables.dto;

import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceivableSaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String description;

    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal amount;

    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Long customerId;
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

    public Long getCustomerId() {
        return customerId;
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

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
