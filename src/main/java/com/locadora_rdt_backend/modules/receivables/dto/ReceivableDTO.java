package com.locadora_rdt_backend.modules.receivables.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class ReceivableDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Instant createdDate;
    private Instant createdAt;
    private Instant updatedAt;
    private String note;
    private String fileName;
    private Boolean paid;
    private BigDecimal remainingBalance;
    private BigDecimal lateFee;
    private BigDecimal lateInterest;
    private BigDecimal discount;
    private BigDecimal fee;
    private BigDecimal subtotal;
    private Boolean residual;
    private Boolean canceled;
    private Long parentReceivableId;
    private Long customerId;
    private String customerName;
    private Long paymentMethodId;
    private String paymentMethodName;
    private Long paymentFrequencyId;
    private String paymentFrequency;
    private Long createdById;
    private String createdByName;
    private Long paidById;
    private String paidByName;

    public ReceivableDTO() {
    }

    public Long getId() {
        return id;
    }

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

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getNote() {
        return note;
    }

    public String getFileName() {
        return fileName;
    }

    public Boolean getPaid() {
        return paid;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public BigDecimal getLateInterest() {
        return lateInterest;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Boolean getResidual() {
        return residual;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public Long getParentReceivableId() {
        return parentReceivableId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public Long getPaymentFrequencyId() {
        return paymentFrequencyId;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public Long getPaidById() {
        return paidById;
    }

    public String getPaidByName() {
        return paidByName;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public void setLateInterest(BigDecimal lateInterest) {
        this.lateInterest = lateInterest;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setResidual(Boolean residual) {
        this.residual = residual;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public void setParentReceivableId(Long parentReceivableId) {
        this.parentReceivableId = parentReceivableId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public void setPaymentFrequencyId(Long paymentFrequencyId) {
        this.paymentFrequencyId = paymentFrequencyId;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public void setPaidById(Long paidById) {
        this.paidById = paidById;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }
}
