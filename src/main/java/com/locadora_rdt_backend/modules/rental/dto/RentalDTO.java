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
    private String status;
    private Instant rentalDate;
    private Instant startDate;
    private Instant expectedReturnDate;
    private BigDecimal totalAmount;
    public Long getId() { return id; }
    public String getRentalNumber() { return rentalNumber; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public Long getRentalTypeId() { return rentalTypeId; }
    public String getRentalTypeName() { return rentalTypeName; }
    public String getStatus() { return status; }
    public Instant getRentalDate() { return rentalDate; }
    public Instant getStartDate() { return startDate; }
    public Instant getExpectedReturnDate() { return expectedReturnDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setId(Long id) { this.id = id; }
    public void setRentalNumber(String rentalNumber) { this.rentalNumber = rentalNumber; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setRentalTypeId(Long rentalTypeId) { this.rentalTypeId = rentalTypeId; }
    public void setRentalTypeName(String rentalTypeName) { this.rentalTypeName = rentalTypeName; }
    public void setStatus(String status) { this.status = status; }
    public void setRentalDate(Instant rentalDate) { this.rentalDate = rentalDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public void setExpectedReturnDate(Instant expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
