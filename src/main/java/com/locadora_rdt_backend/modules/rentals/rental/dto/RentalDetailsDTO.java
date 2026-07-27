package com.locadora_rdt_backend.modules.rentals.rental.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RentalDetailsDTO extends RentalDTO {
    private Long paymentMethodId;
    private String paymentMethodName;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal shippingFee;
    private BigDecimal additionalFee;
    private BigDecimal downPayment;
    private BigDecimal remainingAmount;
    private String deliveryAddress;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private List<RentalItemDTO> items = new ArrayList<>();
    public Long getPaymentMethodId() { return paymentMethodId; }
    public String getPaymentMethodName() { return paymentMethodName; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public BigDecimal getAdditionalFee() { return additionalFee; }
    public BigDecimal getDownPayment() { return downPayment; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public List<RentalItemDTO> getItems() { return items; }
    public void setPaymentMethodId(Long v) { paymentMethodId = v; }
    public void setPaymentMethodName(String v) { paymentMethodName = v; }
    public void setSubtotal(BigDecimal v) { subtotal = v; }
    public void setDiscount(BigDecimal v) { discount = v; }
    public void setShippingFee(BigDecimal v) { shippingFee = v; }
    public void setAdditionalFee(BigDecimal v) { additionalFee = v; }
    public void setDownPayment(BigDecimal v) { downPayment = v; }
    public void setRemainingAmount(BigDecimal v) { remainingAmount = v; }
    public void setDeliveryAddress(String v) { deliveryAddress = v; }
    public void setNotes(String v) { notes = v; }
    public void setCreatedAt(Instant v) { createdAt = v; }
    public void setUpdatedAt(Instant v) { updatedAt = v; }
    public void setCreatedBy(String v) { createdBy = v; }
    public void setUpdatedBy(String v) { updatedBy = v; }
    public void setItems(List<RentalItemDTO> v) { items = v; }
}
