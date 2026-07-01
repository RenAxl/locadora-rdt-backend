package com.locadora_rdt_backend.modules.receivables.dto;

import com.locadora_rdt_backend.modules.receivables.model.ReceivableFile;
import com.locadora_rdt_backend.shared.dto.StoredFileDTO;

public class ReceivableFileDTO extends StoredFileDTO {
    private static final long serialVersionUID = 1L;

    private Long receivableId;

    public ReceivableFileDTO() {
        super();
    }

    public ReceivableFileDTO(ReceivableFile entity) {
        super(entity);
        receivableId = entity.getReceivable().getId();
    }

    public Long getReceivableId() {
        return receivableId;
    }

    public void setReceivableId(Long receivableId) {
        this.receivableId = receivableId;
    }
}
