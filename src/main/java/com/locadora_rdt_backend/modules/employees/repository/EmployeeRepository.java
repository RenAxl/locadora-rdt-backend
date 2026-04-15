package com.locadora_rdt_backend.modules.employees.repository;

import com.locadora_rdt_backend.modules.employees.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("select employee from Employee employee where employee.name like %?1%")
    Page<Employee> find(String name, Pageable pageable);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Employee findByEmployeeCode(String employeeCode);

    Employee findByEmail(String email);

    Employee findByPhone(String phone);

    @Modifying
    @Query("DELETE FROM Employee employee WHERE employee.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE Employee employee SET employee.active = :active WHERE employee.id = :id")
    int updateActiveById(@Param("id") Long id,
                         @Param("active") boolean active);
}
