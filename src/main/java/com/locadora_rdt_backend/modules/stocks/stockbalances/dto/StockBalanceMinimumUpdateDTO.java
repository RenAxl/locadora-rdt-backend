package com.locadora_rdt_backend.modules.stocks.stockbalances.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class StockBalanceMinimumUpdateDTO {

    @NotNull(message = "Campo requerido")
    @Min(value = 0, message = "A quantidade mínima não pode ser negativa")
    private Integer minimumQuantity;

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }
}
