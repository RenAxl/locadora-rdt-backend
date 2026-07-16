package com.locadora_rdt_backend.modules.rental.model;

import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.rentaltypes.model.RentalType;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
@Entity
@Table(name = "tb_rental")
public class Rental implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "rental_number", unique = true, nullable = false, length = 30)
    private String rentalNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_type_id", nullable = false)
    private RentalType rentalType;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "rental_date", nullable = false)
    private Instant rentalDate;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "expected_return_date", nullable = false)
    private Instant expectedReturnDate;

    @Column(name = "actual_return_date")
    private Instant actualReturnDate;

    @Column(name = "delivery_date")
    private Instant deliveryDate;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discount;

    @Column(name = "shipping_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "additional_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal additionalFee;

    @Column(name = "late_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal lateFee;

    @Column(name = "damage_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal damageFee;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "down_payment", nullable = false, precision = 12, scale = 2)
    private BigDecimal downPayment;

    @Column(name = "remaining_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "contract_generated", nullable = false)
    private Boolean contractGenerated;

    @Column(name = "whatsapp_sent", nullable = false)
    private Boolean whatsappSent;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    public Rental() {
    }

    public Rental(Long id, String rentalNumber) {
        this.id = id;
        this.rentalNumber = rentalNumber;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getRentalNumber() {
        return rentalNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public RentalType getRentalType() {
        return rentalType;
    }

    public String getStatus() {
        return status;
    }

    public Instant getRentalDate() {
        return rentalDate;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public Instant getActualReturnDate() {
        return actualReturnDate;
    }

    public Instant getDeliveryDate() {
        return deliveryDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public BigDecimal getAdditionalFee() {
        return additionalFee;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public BigDecimal getDamageFee() {
        return damageFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getDownPayment() {
        return downPayment;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getNotes() {
        return notes;
    }

    public Boolean getContractGenerated() {
        return contractGenerated;
    }

    public Boolean getWhatsappSent() {
        return whatsappSent;
    }

    public Boolean getActive() {
        return active;
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

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setRentalNumber(String rentalNumber) {
        this.rentalNumber = rentalNumber;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setRentalType(RentalType rentalType) {
        this.rentalType = rentalType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRentalDate(Instant rentalDate) {
        this.rentalDate = rentalDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public void setExpectedReturnDate(Instant expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public void setActualReturnDate(Instant actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public void setDeliveryDate(Instant deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public void setAdditionalFee(BigDecimal additionalFee) {
        this.additionalFee = additionalFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public void setDamageFee(BigDecimal damageFee) {
        this.damageFee = damageFee;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDownPayment(BigDecimal downPayment) {
        this.downPayment = downPayment;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setContractGenerated(Boolean contractGenerated) {
        this.contractGenerated = contractGenerated;
    }

    public void setWhatsappSent(Boolean whatsappSent) {
        this.whatsappSent = whatsappSent;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rental)) {
            return false;
        }
        Rental other = (Rental) obj;
        return id != null && id.equals(other.id);
    }
}
