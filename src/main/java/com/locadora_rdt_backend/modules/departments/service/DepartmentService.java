package com.locadora_rdt_backend.modules.departments.service;

import com.locadora_rdt_backend.modules.departments.dto.DepartmentDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentDetailsDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentInsertDTO;
import com.locadora_rdt_backend.modules.departments.dto.DepartmentUpdateDTO;
import com.locadora_rdt_backend.modules.departments.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface DepartmentService {

    Page<DepartmentDTO> findAllPaged(String name, PageRequest pageRequest);

    DepartmentDetailsDTO findById(Long id);

    Department findEntityById(Long id);

    DepartmentDTO insert(DepartmentInsertDTO dto);

    DepartmentDTO update(Long id, DepartmentUpdateDTO dto);

    void delete(Long id);
}
