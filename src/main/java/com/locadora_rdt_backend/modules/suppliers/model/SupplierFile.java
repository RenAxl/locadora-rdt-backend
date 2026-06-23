package com.locadora_rdt_backend.modules.suppliers.model;

import com.locadora_rdt_backend.shared.model.StoredFile;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_supplier_file")
public class SupplierFile extends StoredFile {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    public SupplierFile() {
        // Required by frameworks and serializers.
    }

    public Supplier getSupplier() { return supplier; }

    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
}
