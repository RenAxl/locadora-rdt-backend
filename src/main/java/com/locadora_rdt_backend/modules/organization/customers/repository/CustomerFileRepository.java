package com.locadora_rdt_backend.modules.organization.customers.repository;

import com.locadora_rdt_backend.modules.organization.customers.model.CustomerFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerFileRepository extends JpaRepository<CustomerFile, Long> {

    List<CustomerFile> findByCustomerIdOrderByIdDesc(Long customerId);

    boolean existsByCustomerIdAndNameIgnoreCase(Long customerId, String name);
}