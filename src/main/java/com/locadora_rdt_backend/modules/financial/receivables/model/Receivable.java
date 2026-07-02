package com.locadora_rdt_backend.modules.financial.receivables.model;

import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.users.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_receivable")
public class Receivable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private BigDecimal amount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    private String note;

    @Column(name = "file_name")
    private String fileName;

    @Column(nullable = false)
    private String status;

    private String reference;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "late_fee")
    private BigDecimal lateFee;

    @Column(name = "late_interest")
    private BigDecimal lateInterest;

    private BigDecimal discount;

    private BigDecimal fee;

    private BigDecimal subtotal;

    @Column(nullable = false)
    private Boolean paid = false;

    @Column(name = "remaining_balance")
    private BigDecimal remainingBalance;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean residual = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean canceled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_frequency_id")
    private PaymentFrequency paymentFrequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frequency_id")
    private PaymentFrequency frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    private User paidBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_receivable_id")
    private Receivable parentReceivable;

    @OneToMany(mappedBy = "parentReceivable")
    private List<Receivable> residuals = new ArrayList<>();

    public Receivable() {
    }

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdDate = now;
        createdAt = now;

        if (paid == null) {
            paid = false;
        }

        if (residual == null) {
            residual = false;
        }

        if (canceled == null) {
            canceled = false;
        }

        syncLegacyFields();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
        syncLegacyFields();
    }

    private void syncLegacyFields() {
        if (frequency == null) {
            frequency = paymentFrequency;
        }

        if (Boolean.TRUE.equals(canceled)) {
            status = "CANCELED";
        } else if (Boolean.TRUE.equals(paid)) {
            status = "PAID";
        } else {
            status = "PENDING";
        }
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

    public String getStatus() {
        return status;
    }

    public String getReference() {
        return reference;
    }

    public Long getReferenceId() {
        return referenceId;
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

    public Boolean getPaid() {
        return paid;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public Boolean getResidual() {
        return residual;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public Customer getCustomer() {
        return customer;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentFrequency getPaymentFrequency() {
        return paymentFrequency;
    }

    public PaymentFrequency getFrequency() {
        return frequency;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public Receivable getParentReceivable() {
        return parentReceivable;
    }

    public List<Receivable> getResiduals() {
        return residuals;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
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

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public void setResidual(Boolean residual) {
        this.residual = residual;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentFrequency(PaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
        this.frequency = paymentFrequency;
    }

    public void setFrequency(PaymentFrequency frequency) {
        this.frequency = frequency;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
    }

    public void setParentReceivable(Receivable parentReceivable) {
        this.parentReceivable = parentReceivable;
    }

    public void setResiduals(List<Receivable> residuals) {
        this.residuals = residuals;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Receivable other = (Receivable) obj;

        if (id == null) {
            return other.id == null;
        }

        return id.equals(other.id);
    }
}
