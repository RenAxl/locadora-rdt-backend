package com.locadora_rdt_backend.modules.financial.payables.dto;

public class PayableUpdateDTO extends PayableSaveDTO {
    private static final long serialVersionUID = 1L;

    private Long id;

    public PayableUpdateDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
