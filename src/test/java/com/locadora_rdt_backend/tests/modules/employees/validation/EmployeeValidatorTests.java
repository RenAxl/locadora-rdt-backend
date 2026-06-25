package com.locadora_rdt_backend.tests.modules.employees.validation;

import com.locadora_rdt_backend.modules.employees.dto.EmployeeInsertDTO;
import com.locadora_rdt_backend.modules.employees.dto.EmployeeUpdateDTO;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.modules.employees.validation.EmployeeInsertValidator;
import com.locadora_rdt_backend.modules.employees.validation.EmployeeUpdateValidator;
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
import java.time.LocalDate;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class EmployeeValidatorTests {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private EmployeeInsertValidator insertValidator;
    private EmployeeUpdateValidator updateValidator;

    @BeforeEach
    void setUp() {
        insertValidator = new EmployeeInsertValidator(repository);
        updateValidator = new EmployeeUpdateValidator(repository, request);
    }

    @Test
    void insertValidatorShouldReturnTrueWhenDtoIsNullOrFieldsAreAvailable() {
        Assertions.assertTrue(insertValidator.isValid(null, context));
        Assertions.assertTrue(insertValidator.isValid(createInsertDTO(), context));
    }

    @Test
    void insertValidatorShouldReturnFalseWhenDuplicatedAndTerminationDateIsBeforeHireDate() {
        EmployeeInsertDTO dto = createInsertDTO();
        dto.setTerminationDate(LocalDate.of(2026, 1, 9));

        mockConstraintViolation();
        Mockito.when(repository.existsByEmail("funcionario@email.com")).thenReturn(true);
        Mockito.when(repository.existsByEmployeeCode("EMP001")).thenReturn(true);
        Mockito.when(repository.existsByPhone("11999999999")).thenReturn(true);

        Assertions.assertFalse(insertValidator.isValid(dto, context));
        Mockito.verify(context, Mockito.times(4)).disableDefaultConstraintViolation();
        Mockito.verify(violationBuilder).addPropertyNode("email");
        Mockito.verify(violationBuilder).addPropertyNode("employeeCode");
        Mockito.verify(violationBuilder).addPropertyNode("phone");
        Mockito.verify(violationBuilder).addPropertyNode("terminationDate");
    }

    @Test
    void updateValidatorShouldReturnTrueWhenFieldsAreAvailableOrBlank() {
        EmployeeUpdateDTO dto = createUpdateDTO();

        mockUriId(1L);
        Assertions.assertTrue(updateValidator.isValid(dto, context));

        EmployeeUpdateDTO blankDTO = createUpdateDTO();
        blankDTO.setEmail(" ");
        blankDTO.setPhone(null);
        blankDTO.setEmployeeCode("");

        Assertions.assertTrue(updateValidator.isValid(blankDTO, context));
        Mockito.verify(repository, Mockito.never()).findByEmail(" ");
        Mockito.verify(repository, Mockito.never()).findByPhone(null);
        Mockito.verify(repository, Mockito.never()).findByEmployeeCode("");
    }

    @Test
    void updateValidatorShouldReturnTrueWhenFieldsBelongToSameEmployee() {
        Employee sameEmployee = employeeWithId(1L);

        mockUriId(1L);
        Mockito.when(repository.findByEmail("funcionario@email.com")).thenReturn(sameEmployee);
        Mockito.when(repository.findByPhone("11999999999")).thenReturn(sameEmployee);
        Mockito.when(repository.findByEmployeeCode("EMP001")).thenReturn(sameEmployee);

        Assertions.assertTrue(updateValidator.isValid(createUpdateDTO(), context));
    }

    @Test
    void updateValidatorShouldReturnFalseWhenDuplicatedAndTerminationDateIsBeforeHireDate() {
        EmployeeUpdateDTO dto = createUpdateDTO();
        dto.setTerminationDate(LocalDate.of(2026, 1, 9));
        Employee otherEmployee = employeeWithId(2L);

        mockUriId(1L);
        mockConstraintViolation();
        Mockito.when(repository.findByEmail("funcionario@email.com")).thenReturn(otherEmployee);
        Mockito.when(repository.findByPhone("11999999999")).thenReturn(otherEmployee);
        Mockito.when(repository.findByEmployeeCode("EMP001")).thenReturn(otherEmployee);

        Assertions.assertFalse(updateValidator.isValid(dto, context));
        Mockito.verify(context, Mockito.times(4)).disableDefaultConstraintViolation();
        Mockito.verify(violationBuilder).addPropertyNode("email");
        Mockito.verify(violationBuilder).addPropertyNode("phone");
        Mockito.verify(violationBuilder).addPropertyNode("employeeCode");
        Mockito.verify(violationBuilder).addPropertyNode("terminationDate");
    }

    private EmployeeInsertDTO createInsertDTO() {
        EmployeeInsertDTO dto = new EmployeeInsertDTO();
        dto.setEmail("funcionario@email.com");
        dto.setEmployeeCode("EMP001");
        dto.setPhone("11999999999");
        dto.setHireDate(LocalDate.of(2026, 1, 10));
        dto.setTerminationDate(LocalDate.of(2026, 1, 11));
        return dto;
    }

    private EmployeeUpdateDTO createUpdateDTO() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO();
        dto.setEmail("funcionario@email.com");
        dto.setEmployeeCode("EMP001");
        dto.setPhone("11999999999");
        dto.setHireDate(LocalDate.of(2026, 1, 10));
        dto.setTerminationDate(LocalDate.of(2026, 1, 11));
        return dto;
    }

    private Employee employeeWithId(Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
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
