package com.locadora_rdt_backend.modules.stocks.stockbalances.dto;

import java.io.Serializable;

public class StockBalanceDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long itemId;
    private String itemName;
    private Integer totalQuantity;
    private Integer reservedQuantity;
    private Integer unavailableQuantity;
    private Integer availableQuantity;
    private Integer minimumQuantity;
    private Boolean lowStock;

    public StockBalanceDTO() {
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public Integer getUnavailableQuantity() {
        return unavailableQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public Boolean getLowStock() {
        return lowStock;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public void setUnavailableQuantity(Integer unavailableQuantity) {
        this.unavailableQuantity = unavailableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public void setLowStock(Boolean lowStock) {
        this.lowStock = lowStock;
    }
}
