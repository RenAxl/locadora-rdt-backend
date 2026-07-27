package com.locadora_rdt_backend.modules.organization.suppliers.dto;

import com.locadora_rdt_backend.modules.organization.suppliers.model.SupplierFile;
import com.locadora_rdt_backend.shared.dto.StoredFileDTO;

public class SupplierFileDTO extends StoredFileDTO {
    private static final long serialVersionUID = 1L;

    private Long supplierId;

    public SupplierFileDTO() {
        super();
    }

    public SupplierFileDTO(SupplierFile entity) {
        super(entity);
        supplierId = entity.getSupplier().getId();
    }

    public Long getSupplierId() { return supplierId; }

    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
}
