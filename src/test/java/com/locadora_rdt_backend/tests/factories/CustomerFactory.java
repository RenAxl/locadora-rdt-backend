package com.locadora_rdt_backend.tests.factories;

import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;

import java.time.Instant;

public class CustomerFactory {

    public static Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Maria Silva");
        customer.setCpf("12345678900");
        customer.setEmail("maria@email.com");
        customer.setPhone("31999999999");
        customer.setAddress("Rua A, 123");
        customer.setActive(true);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        return customer;
    }

    public static CustomerDTO createCustomerDTO() {
        return new CustomerDTO(createCustomer());
    }
}