package com.locadora_rdt_backend.modules.stocks.stockmovements.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StockMovementInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Campo requerido")
    private Long itemId;

    @NotBlank(message = "Campo requerido")
    private String type;

    @NotNull(message = "Campo requerido")
    @Min(value = 1, message = "A quantidade deve ser maior que zero")
    private Integer quantity;

    private String reason;
    private String referenceType;
    private Long referenceId;

    public StockMovementInsertDTO() {
    }

    public Long getItemId() {
        return itemId;
    }

    public String getType() {
        return type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}
