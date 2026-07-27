package com.locadora_rdt_backend.modules.organization.customers.dto;

import com.locadora_rdt_backend.modules.organization.customers.model.CustomerFile;
import com.locadora_rdt_backend.shared.dto.StoredFileDTO;

public class CustomerFileDTO extends StoredFileDTO {
    private static final long serialVersionUID = 1L;

    private Long customerId;

    public CustomerFileDTO() {
        super();
    }

    public CustomerFileDTO(CustomerFile entity) {
        super(entity);
        customerId = entity.getCustomer().getId();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
