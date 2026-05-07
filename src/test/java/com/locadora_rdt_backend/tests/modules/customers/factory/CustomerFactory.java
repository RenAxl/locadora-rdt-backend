package com.locadora_rdt_backend.tests.modules.customers.factory;

import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDetailsDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
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
        customer.setCreatedBy("SYSTEM");
        customer.setUpdatedBy("SYSTEM");
        return customer;
    }

    public static Customer createCustomer(Long id) {
        Customer customer = createCustomer();
        customer.setId(id);
        return customer;
    }

    public static CustomerDTO createCustomerDTO() {
        return createCustomerDTO(createCustomer());
    }

    public static CustomerDTO createCustomerDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setCpf(customer.getCpf());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setActive(customer.getActive());
        return dto;
    }

    public static CustomerDetailsDTO createCustomerDetailsDTO() {
        return createCustomerDetailsDTO(createCustomer());
    }

    public static CustomerDetailsDTO createCustomerDetailsDTO(Customer customer) {
        CustomerDetailsDTO dto = new CustomerDetailsDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setCpf(customer.getCpf());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setActive(customer.getActive());
        return dto;
    }

    public static CustomerInsertDTO createCustomerInsertDTO() {
        CustomerInsertDTO dto = new CustomerInsertDTO();
        dto.setName("Maria Silva");
        dto.setCpf("12345678900");
        dto.setEmail("maria@email.com");
        dto.setPhone("31999999999");
        dto.setAddress("Rua A, 123");
        return dto;
    }

    public static CustomerUpdateDTO createCustomerUpdateDTO() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setName("Maria Atualizada");
        dto.setCpf("12345678900");
        dto.setEmail("maria.atualizada@email.com");
        dto.setPhone("31888888888");
        dto.setAddress("Rua B, 456");
        return dto;
    }
}