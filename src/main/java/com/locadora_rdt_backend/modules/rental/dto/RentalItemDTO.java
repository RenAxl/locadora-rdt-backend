package com.locadora_rdt_backend.modules.rental.dto;

import java.math.BigDecimal;

public class RentalItemDTO {
    private Long id;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal additionalFee;
    private BigDecimal subtotal;
    public Long getId() { return id; }
    public Long getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getAdditionalFee() { return additionalFee; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setId(Long id) { this.id = id; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public void setAdditionalFee(BigDecimal additionalFee) { this.additionalFee = additionalFee; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
