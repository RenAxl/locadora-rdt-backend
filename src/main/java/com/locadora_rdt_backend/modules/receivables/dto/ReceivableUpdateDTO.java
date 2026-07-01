package com.locadora_rdt_backend.modules.receivables.dto;

public class ReceivableUpdateDTO extends ReceivableSaveDTO {
    private static final long serialVersionUID = 1L;

    private Long id;

    public ReceivableUpdateDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
