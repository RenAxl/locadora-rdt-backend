package com.locadora_rdt_backend.modules.organization.employees.dto;

import com.locadora_rdt_backend.shared.dto.FileViewDTO;

public class EmployeeFileViewDTO extends FileViewDTO {
    private static final long serialVersionUID = 1L;

    public EmployeeFileViewDTO() {
        super();
    }

    public EmployeeFileViewDTO(String fileName, String contentType, byte[] data) {
        super(fileName, contentType, data);
    }
}
