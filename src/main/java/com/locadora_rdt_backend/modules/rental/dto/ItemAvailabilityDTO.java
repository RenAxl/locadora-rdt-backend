package com.locadora_rdt_backend.modules.rental.dto;

public class ItemAvailabilityDTO {
    private Long itemId;
    private String itemName;
    private Long availableQuantity;
    private Long reservedQuantity;
    private Long rentedQuantity;

    public Long getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public Long getAvailableQuantity() { return availableQuantity; }
    public Long getReservedQuantity() { return reservedQuantity; }
    public Long getRentedQuantity() { return rentedQuantity; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setAvailableQuantity(Long availableQuantity) { this.availableQuantity = availableQuantity; }
    public void setReservedQuantity(Long reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    public void setRentedQuantity(Long rentedQuantity) { this.rentedQuantity = rentedQuantity; }
}
