package com.locadora_rdt_backend.modules.employees.departments.repository;

import com.locadora_rdt_backend.modules.employees.departments.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("select department from Department department where department.name like %?1%")
    Page<Department> find(String name, Pageable pageable);

}
