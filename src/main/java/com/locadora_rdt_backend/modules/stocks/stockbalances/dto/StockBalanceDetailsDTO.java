package com.locadora_rdt_backend.modules.stocks.stockbalances.dto;

public class StockBalanceDetailsDTO extends StockBalanceDTO {
    private static final long serialVersionUID = 1L;

    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
