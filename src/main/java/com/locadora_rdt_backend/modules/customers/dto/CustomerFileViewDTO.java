package com.locadora_rdt_backend.modules.customers.dto;

import com.locadora_rdt_backend.shared.dto.FileViewDTO;

public class CustomerFileViewDTO extends FileViewDTO {
    private static final long serialVersionUID = 1L;

    public CustomerFileViewDTO() {
        super();
    }

    public CustomerFileViewDTO(String fileName, String contentType, byte[] data) {
        super(fileName, contentType, data);
    }
}
