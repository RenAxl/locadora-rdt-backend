package com.locadora_rdt_backend.modules.financial.payables.dto;

import com.locadora_rdt_backend.modules.financial.payables.model.PayableFile;
import com.locadora_rdt_backend.shared.dto.StoredFileDTO;

public class PayableFileDTO extends StoredFileDTO {
    private static final long serialVersionUID = 1L;

    private Long payableId;

    public PayableFileDTO() {
        super();
    }

    public PayableFileDTO(PayableFile entity) {
        super(entity);
        payableId = entity.getPayable().getId();
    }

    public Long getPayableId() {
        return payableId;
    }

    public void setPayableId(Long payableId) {
        this.payableId = payableId;
    }
}
