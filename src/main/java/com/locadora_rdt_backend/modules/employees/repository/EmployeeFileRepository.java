package com.locadora_rdt_backend.modules.employees.repository;

import com.locadora_rdt_backend.modules.employees.model.EmployeeFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeFileRepository extends JpaRepository<EmployeeFile, Long> {

    List<EmployeeFile> findByEmployeeIdOrderByIdDesc(Long employeeId);

    boolean existsByEmployeeIdAndNameIgnoreCase(Long employeeId, String name);
}