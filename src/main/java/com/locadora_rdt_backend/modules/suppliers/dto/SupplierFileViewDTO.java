package com.locadora_rdt_backend.modules.suppliers.dto;

import com.locadora_rdt_backend.shared.dto.FileViewDTO;

public class SupplierFileViewDTO extends FileViewDTO {
    private static final long serialVersionUID = 1L;

    public SupplierFileViewDTO() {
        super();
    }

    public SupplierFileViewDTO(String fileName, String contentType, byte[] data) {
        super(fileName, contentType, data);
    }
}
