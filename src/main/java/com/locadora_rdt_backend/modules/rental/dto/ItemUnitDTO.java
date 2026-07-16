package com.locadora_rdt_backend.modules.rental.dto;

public class ItemUnitDTO {
    private Long id;
    private Long itemId;
    private String itemName;
    private String assetCode;
    private String serialNumber;
    private String status;
    private String conditionStatus;
    private Boolean active;

    public Long getId() { return id; }
    public Long getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getAssetCode() { return assetCode; }
    public String getSerialNumber() { return serialNumber; }
    public String getStatus() { return status; }
    public String getConditionStatus() { return conditionStatus; }
    public Boolean getActive() { return active; }
    public void setId(Long id) { this.id = id; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setAssetCode(String assetCode) { this.assetCode = assetCode; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public void setStatus(String status) { this.status = status; }
    public void setConditionStatus(String conditionStatus) { this.conditionStatus = conditionStatus; }
    public void setActive(Boolean active) { this.active = active; }
}
