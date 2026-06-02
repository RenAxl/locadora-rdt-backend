package com.locadora_rdt_backend.modules.suppliers.repository;

import com.locadora_rdt_backend.modules.suppliers.model.SupplierFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierFileRepository extends JpaRepository<SupplierFile, Long> {

    List<SupplierFile> findBySupplierIdOrderByIdDesc(Long supplierId);
}
