package com.locadora_rdt_backend.modules.rentals.rental.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RentalSaveDTO {
    private Long customerId;
    @NotNull private Long rentalTypeId;
    private Long paymentMethodId;
    @NotNull private Instant startDate;
    @NotNull private Instant expectedReturnDate;
    private BigDecimal discount;
    private BigDecimal shippingFee;
    private BigDecimal additionalFee;
    private BigDecimal downPayment;
    private String deliveryAddress;
    private String notes;
    @Valid private List<RentalItemSaveDTO> items = new ArrayList<>();

    public Long getCustomerId() { return customerId; }
    public Long getRentalTypeId() { return rentalTypeId; }
    public Long getPaymentMethodId() { return paymentMethodId; }
    public Instant getStartDate() { return startDate; }
    public Instant getExpectedReturnDate() { return expectedReturnDate; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public BigDecimal getAdditionalFee() { return additionalFee; }
    public BigDecimal getDownPayment() { return downPayment; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getNotes() { return notes; }
    public List<RentalItemSaveDTO> getItems() { return items; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setRentalTypeId(Long rentalTypeId) { this.rentalTypeId = rentalTypeId; }
    public void setPaymentMethodId(Long paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public void setExpectedReturnDate(Instant expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public void setAdditionalFee(BigDecimal additionalFee) { this.additionalFee = additionalFee; }
    public void setDownPayment(BigDecimal downPayment) { this.downPayment = downPayment; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setItems(List<RentalItemSaveDTO> items) { this.items = items; }
}
