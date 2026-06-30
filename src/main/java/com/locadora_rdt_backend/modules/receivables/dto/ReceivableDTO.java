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
    private String fileName;
    private Boolean paid;
    private BigDecimal remainingBalance;
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

    public String getFileName() {
        return fileName;
    }

    public Boolean getPaid() {
        return paid;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
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

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
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
