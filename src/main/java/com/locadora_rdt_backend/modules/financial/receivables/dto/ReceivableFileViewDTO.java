package com.locadora_rdt_backend.modules.financial.receivables.dto;

import com.locadora_rdt_backend.shared.dto.FileViewDTO;

public class ReceivableFileViewDTO extends FileViewDTO {
    private static final long serialVersionUID = 1L;

    public ReceivableFileViewDTO() {
        super();
    }

    public ReceivableFileViewDTO(String fileName, String contentType, byte[] data) {
        super(fileName, contentType, data);
    }
}
