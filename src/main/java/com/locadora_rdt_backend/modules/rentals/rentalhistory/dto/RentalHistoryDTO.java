package com.locadora_rdt_backend.modules.rentals.rentalhistory.dto;

import com.locadora_rdt_backend.modules.rentals.rental.dto.RentalItemDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RentalHistoryDTO {
    private Long id;
    private String rentalNumber;
    private String rentalTypeName;
    private String status;
    private Instant rentalDate;
    private Instant startDate;
    private Instant expectedReturnDate;
    private Instant actualReturnDate;
    private BigDecimal totalAmount;
    private Boolean paid;
    private List<RentalItemDTO> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getRentalNumber() {
        return rentalNumber;
    }

    public String getRentalTypeName() {
        return rentalTypeName;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Boolean getPaid() {
        return paid;
    }

    public List<RentalItemDTO> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRentalNumber(String rentalNumber) {
        this.rentalNumber = rentalNumber;
    }

    public void setRentalTypeName(String rentalTypeName) {
        this.rentalTypeName = rentalTypeName;
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

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public void setItems(List<RentalItemDTO> items) {
        this.items = items;
    }

}
