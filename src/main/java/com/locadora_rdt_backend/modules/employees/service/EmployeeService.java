package com.locadora_rdt_backend.modules.employees.service;

import com.locadora_rdt_backend.modules.employees.dto.*;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    Page<EmployeeDTO> findAllPaged(String name, PageRequest pageRequest);

    EmployeeDetailsDTO findById(Long id);

    EmployeeDTO insert(EmployeeInsertDTO dto);

    EmployeeDTO update(Long id, EmployeeUpdateDTO dto);

    void updatePhoto(Long id, MultipartFile file);

    Employee findEntityById(Long id);

    void delete(Long id);

    void deleteAll(List<Long> ids);

    void changeActiveStatus(Long id, boolean active);
}
