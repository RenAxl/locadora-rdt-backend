package com.locadora_rdt_backend.modules.customers.repository;

import com.locadora_rdt_backend.modules.customers.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select customer from Customer customer where customer.name like %?1%")
    Page<Customer> find(String name, Pageable pageable);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Customer findByCpf(String cpf);

    Customer findByEmail(String email);

    Customer findByPhone(String phone);

    @Modifying
    @Query("DELETE FROM Customer c WHERE c.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

}
