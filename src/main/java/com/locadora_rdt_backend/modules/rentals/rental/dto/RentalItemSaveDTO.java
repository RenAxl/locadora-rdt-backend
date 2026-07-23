package com.locadora_rdt_backend.modules.rentals.rental.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RentalItemSaveDTO {
    @NotNull
    private Long itemId;
    @NotNull @Min(1)
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal additionalFee;

    public Long getItemId() { return itemId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getAdditionalFee() { return additionalFee; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public void setAdditionalFee(BigDecimal additionalFee) { this.additionalFee = additionalFee; }
}
