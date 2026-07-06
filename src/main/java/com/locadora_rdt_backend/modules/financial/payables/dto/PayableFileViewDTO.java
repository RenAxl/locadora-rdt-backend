package com.locadora_rdt_backend.modules.financial.payables.dto;

import com.locadora_rdt_backend.shared.dto.FileViewDTO;

public class PayableFileViewDTO extends FileViewDTO {
    private static final long serialVersionUID = 1L;

    public PayableFileViewDTO() {
        super();
    }

    public PayableFileViewDTO(String fileName, String contentType, byte[] data) {
        super(fileName, contentType, data);
    }
}
