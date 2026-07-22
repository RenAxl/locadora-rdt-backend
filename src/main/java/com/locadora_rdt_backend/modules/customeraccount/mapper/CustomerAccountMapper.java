package com.locadora_rdt_backend.modules.customeraccount.mapper;

import com.locadora_rdt_backend.modules.customeraccount.dto.CustomerAccountRegistrationDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerAccountMapper {
    public Customer toCustomer(CustomerAccountRegistrationDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName().trim());
        customer.setCpf(onlyNumbers(dto.getCpf()));
        customer.setEmail(dto.getEmail().trim().toLowerCase());
        customer.setPhone(onlyNumbers(dto.getPhone()));
        customer.setAddress(toCustomerAddress(dto));
        customer.setActive(true);
        customer.setCreatedBy("CUSTOMER_REGISTRATION");
        return customer;
    }

    public User toUser(CustomerAccountRegistrationDTO dto) {
        User user = new User();
        user.setName(dto.getName().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setTelephone(onlyNumbers(dto.getPhone()));
        user.setAddress(toUserAddress(dto));
        user.setPassword(null);
        user.setActive(false);
        user.setCreatedBy("CUSTOMER_REGISTRATION");
        return user;
    }

    private com.locadora_rdt_backend.modules.customers.model.Address toCustomerAddress(CustomerAccountRegistrationDTO dto) {
        com.locadora_rdt_backend.modules.customers.model.Address address =
                new com.locadora_rdt_backend.modules.customers.model.Address();
        fillCustomerAddress(address, dto);
        return address;
    }

    private void fillCustomerAddress(com.locadora_rdt_backend.modules.customers.model.Address address,
                                     CustomerAccountRegistrationDTO dto) {
        address.setStreet(dto.getStreet().trim());
        address.setNumber(dto.getNumber().trim());
        address.setComplement(trim(dto.getComplement()));
        address.setNeighborhood(dto.getNeighborhood().trim());
        address.setCity(dto.getCity().trim());
        address.setState(dto.getState().trim().toUpperCase());
        address.setZipCode(dto.getZipCode().trim());
    }

    private com.locadora_rdt_backend.modules.users.model.Address toUserAddress(CustomerAccountRegistrationDTO dto) {
        com.locadora_rdt_backend.modules.users.model.Address address =
                new com.locadora_rdt_backend.modules.users.model.Address();
        address.setStreet(dto.getStreet().trim());
        address.setNumber(dto.getNumber().trim());
        address.setComplement(trim(dto.getComplement()));
        address.setNeighborhood(dto.getNeighborhood().trim());
        address.setCity(dto.getCity().trim());
        address.setState(dto.getState().trim().toUpperCase());
        address.setZipCode(dto.getZipCode().trim());
        return address;
    }

    private String onlyNumbers(String value) { return value == null ? null : value.replaceAll("\\D", ""); }
    private String trim(String value) { return value == null || value.trim().isEmpty() ? null : value.trim(); }
}
