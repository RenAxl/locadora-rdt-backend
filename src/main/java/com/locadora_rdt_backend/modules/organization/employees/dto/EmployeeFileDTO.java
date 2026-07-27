package com.locadora_rdt_backend.modules.organization.employees.dto;

import com.locadora_rdt_backend.modules.organization.employees.model.EmployeeFile;
import com.locadora_rdt_backend.shared.dto.StoredFileDTO;

public class EmployeeFileDTO extends StoredFileDTO {
    private static final long serialVersionUID = 1L;

    private Long employeeId;

    public EmployeeFileDTO() {
        super();
    }

    public EmployeeFileDTO(EmployeeFile entity) {
        super(entity);
        employeeId = entity.getEmployee().getId();
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
