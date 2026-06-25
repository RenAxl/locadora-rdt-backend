package com.locadora_rdt_backend.tests.modules.customers.validation;

import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.validation.CustomerInsertValidator;
import com.locadora_rdt_backend.modules.customers.validation.CustomerUpdateValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CustomerValidatorTests {

    @Mock
    private CustomerRepository repository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private CustomerInsertValidator insertValidator;
    private CustomerUpdateValidator updateValidator;

    @BeforeEach
    void setUp() {
        insertValidator = new CustomerInsertValidator(repository);
        updateValidator = new CustomerUpdateValidator(repository, request);
    }

    @Test
    void insertValidatorShouldReturnTrueWhenFieldsAreAvailable() {
        Assertions.assertTrue(insertValidator.isValid(createInsertDTO(), context));
    }

    @Test
    void insertValidatorShouldReturnFalseWhenEmailCpfAndPhoneAlreadyExist() {
        CustomerInsertDTO dto = createInsertDTO();

        mockConstraintViolation();
        Mockito.when(repository.existsByEmail("cliente@email.com")).thenReturn(true);
        Mockito.when(repository.existsByCpf("123")).thenReturn(true);
        Mockito.when(repository.existsByPhone("11999999999")).thenReturn(true);

        Assertions.assertFalse(insertValidator.isValid(dto, context));
        Mockito.verify(context, Mockito.times(3)).disableDefaultConstraintViolation();
        Mockito.verify(violationBuilder).addPropertyNode("email");
        Mockito.verify(violationBuilder).addPropertyNode("cpf");
        Mockito.verify(violationBuilder).addPropertyNode("phone");
    }

    @Test
    void updateValidatorShouldReturnTrueWhenFieldsAreAvailable() {
        mockUriId(1L);

        Assertions.assertTrue(updateValidator.isValid(createUpdateDTO(), context));
    }

    @Test
    void updateValidatorShouldReturnTrueWhenFieldsBelongToSameCustomer() {
        Customer sameCustomer = customerWithId(1L);

        mockUriId(1L);
        Mockito.when(repository.findByEmail("cliente@email.com")).thenReturn(sameCustomer);
        Mockito.when(repository.findByPhone("11999999999")).thenReturn(sameCustomer);
        Mockito.when(repository.findByCpf("123")).thenReturn(sameCustomer);

        Assertions.assertTrue(updateValidator.isValid(createUpdateDTO(), context));
    }

    @Test
    void updateValidatorShouldReturnFalseWhenFieldsBelongToAnotherCustomer() {
        Customer otherCustomer = customerWithId(2L);

        mockUriId(1L);
        mockConstraintViolation();
        Mockito.when(repository.findByEmail("cliente@email.com")).thenReturn(otherCustomer);
        Mockito.when(repository.findByPhone("11999999999")).thenReturn(otherCustomer);
        Mockito.when(repository.findByCpf("123")).thenReturn(otherCustomer);

        Assertions.assertFalse(updateValidator.isValid(createUpdateDTO(), context));
        Mockito.verify(context, Mockito.times(3)).disableDefaultConstraintViolation();
        Mockito.verify(violationBuilder).addPropertyNode("email");
        Mockito.verify(violationBuilder).addPropertyNode("telephone");
        Mockito.verify(violationBuilder).addPropertyNode("cpf");
    }

    private CustomerInsertDTO createInsertDTO() {
        CustomerInsertDTO dto = new CustomerInsertDTO();
        dto.setEmail("cliente@email.com");
        dto.setCpf("123");
        dto.setPhone("11999999999");
        return dto;
    }

    private CustomerUpdateDTO createUpdateDTO() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setEmail("cliente@email.com");
        dto.setCpf("123");
        dto.setPhone("11999999999");
        return dto;
    }

    private Customer customerWithId(Long id) {
        Customer customer = new Customer();
        customer.setId(id);
        return customer;
    }

    private void mockUriId(Long id) {
        Mockito.when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", id.toString()));
    }

    private void mockConstraintViolation() {
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(violationBuilder);
        Mockito.when(violationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilder);
        Mockito.when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }
}
