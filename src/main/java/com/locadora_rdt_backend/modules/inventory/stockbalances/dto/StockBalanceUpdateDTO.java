package com.locadora_rdt_backend.modules.inventory.stockbalances.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StockBalanceUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Campo requerido")
    @Min(value = 0, message = "A quantidade total não pode ser negativa")
    private Integer totalQuantity;

    @NotNull(message = "Campo requerido")
    @Min(value = 0, message = "A quantidade reservada não pode ser negativa")
    private Integer reservedQuantity;

    @NotNull(message = "Campo requerido")
    @Min(value = 0, message = "A quantidade indisponível não pode ser negativa")
    private Integer unavailableQuantity;

    @NotNull(message = "Campo requerido")
    @Min(value = 0, message = "A quantidade mínima não pode ser negativa")
    private Integer minimumQuantity;

    public StockBalanceUpdateDTO() {
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

    public Integer getMinimumQuantity() {
        return minimumQuantity;
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

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }
}
