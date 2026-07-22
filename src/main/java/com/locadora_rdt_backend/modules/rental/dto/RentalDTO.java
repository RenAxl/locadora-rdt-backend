package com.locadora_rdt_backend.modules.rental.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class RentalDTO {
    private Long id;
    private String rentalNumber;
    private Long customerId;
    private String customerName;
    private Long rentalTypeId;
    private String rentalTypeName;
    private Long paymentMethodId;
    private String paymentMethodName;
    private String status;
    private Instant rentalDate;
    private Instant startDate;
    private Instant expectedReturnDate;
    private Instant actualReturnDate;
    private Boolean paid;
    private Boolean whatsappSent;
    private BigDecimal totalAmount;
    private Long overdueDays;
    private BigDecimal lateFeePerDay;
    private BigDecimal calculatedLateFee;
    private BigDecimal totalWithLateFee;
    public Long getId() { return id; }
    public String getRentalNumber() { return rentalNumber; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public Long getRentalTypeId() { return rentalTypeId; }
    public String getRentalTypeName() { return rentalTypeName; }
    public Long getPaymentMethodId() { return paymentMethodId; }
    public String getPaymentMethodName() { return paymentMethodName; }
    public String getStatus() { return status; }
    public Instant getRentalDate() { return rentalDate; }
    public Instant getStartDate() { return startDate; }
    public Instant getExpectedReturnDate() { return expectedReturnDate; }
    public Instant getActualReturnDate() { return actualReturnDate; }
    public Boolean getPaid() { return paid; }
    public Boolean getWhatsappSent() { return whatsappSent; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Long getOverdueDays() { return overdueDays; }
    public BigDecimal getLateFeePerDay() { return lateFeePerDay; }
    public BigDecimal getCalculatedLateFee() { return calculatedLateFee; }
    public BigDecimal getTotalWithLateFee() { return totalWithLateFee; }
    public void setId(Long id) { this.id = id; }
    public void setRentalNumber(String rentalNumber) { this.rentalNumber = rentalNumber; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setRentalTypeId(Long rentalTypeId) { this.rentalTypeId = rentalTypeId; }
    public void setRentalTypeName(String rentalTypeName) { this.rentalTypeName = rentalTypeName; }
    public void setPaymentMethodId(Long paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public void setPaymentMethodName(String paymentMethodName) { this.paymentMethodName = paymentMethodName; }
    public void setStatus(String status) { this.status = status; }
    public void setRentalDate(Instant rentalDate) { this.rentalDate = rentalDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public void setExpectedReturnDate(Instant expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }
    public void setActualReturnDate(Instant actualReturnDate) { this.actualReturnDate = actualReturnDate; }
    public void setPaid(Boolean paid) { this.paid = paid; }
    public void setWhatsappSent(Boolean whatsappSent) { this.whatsappSent = whatsappSent; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setOverdueDays(Long overdueDays) { this.overdueDays = overdueDays; }
    public void setLateFeePerDay(BigDecimal lateFeePerDay) { this.lateFeePerDay = lateFeePerDay; }
    public void setCalculatedLateFee(BigDecimal calculatedLateFee) { this.calculatedLateFee = calculatedLateFee; }
    public void setTotalWithLateFee(BigDecimal totalWithLateFee) { this.totalWithLateFee = totalWithLateFee; }
}
