package com.locadora_rdt_backend.modules.organization.suppliers.service;

import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierDetailsDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierInsertDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.dto.SupplierUpdateDTO;
import com.locadora_rdt_backend.modules.organization.suppliers.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface SupplierService {

    Page<SupplierDTO> findAllPaged(String name, PageRequest pageRequest);

    SupplierDetailsDTO findById(Long id);

    Supplier findEntityById(Long id);

    SupplierDTO insert(SupplierInsertDTO dto);

    SupplierDTO update(Long id, SupplierUpdateDTO dto);

    void updateImage(Long id, MultipartFile file);

    void delete(Long id);
}
