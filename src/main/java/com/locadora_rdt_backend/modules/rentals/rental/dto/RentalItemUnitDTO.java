package com.locadora_rdt_backend.modules.rentals.rental.dto;

import java.time.Instant;

public class RentalItemUnitDTO {
    private Long id;
    private Long rentalItemId;
    private Long itemUnitId;
    private String itemName;
    private String assetCode;
    private String status;
    private Instant reservedAt;
    private Instant deliveredAt;
    private Instant returnedAt;

    public Long getId() { return id; }
    public Long getRentalItemId() { return rentalItemId; }
    public Long getItemUnitId() { return itemUnitId; }
    public String getItemName() { return itemName; }
    public String getAssetCode() { return assetCode; }
    public String getStatus() { return status; }
    public Instant getReservedAt() { return reservedAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public Instant getReturnedAt() { return returnedAt; }
    public void setId(Long id) { this.id = id; }
    public void setRentalItemId(Long rentalItemId) { this.rentalItemId = rentalItemId; }
    public void setItemUnitId(Long itemUnitId) { this.itemUnitId = itemUnitId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }
    public void setStatus(String status) { this.status = status; }
    public void setReservedAt(Instant reservedAt) { this.reservedAt = reservedAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
    public void setReturnedAt(Instant returnedAt) { this.returnedAt = returnedAt; }
}
