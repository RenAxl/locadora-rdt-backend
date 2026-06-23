package com.locadora_rdt_backend.tests.modules.users.validation;

import com.locadora_rdt_backend.modules.users.dto.UserInsertDTO;
import com.locadora_rdt_backend.modules.users.dto.UserUpdateDTO;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import com.locadora_rdt_backend.modules.users.validation.UserInsertValidator;
import com.locadora_rdt_backend.modules.users.validation.UserUpdateValidator;
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
class UserValidatorTests {

    @Mock
    private UserRepository repository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private UserInsertValidator insertValidator;
    private UserUpdateValidator updateValidator;

    @BeforeEach
    void setUp() {
        insertValidator = new UserInsertValidator(repository);
        updateValidator = new UserUpdateValidator(request, repository);
    }

    @Test
    void insertValidatorShouldReturnTrueWhenEmailAndTelephoneAreAvailable() {
        UserInsertDTO dto = createInsertDTO();

        Assertions.assertTrue(insertValidator.isValid(dto, context));
    }

    @Test
    void insertValidatorShouldReturnFalseWhenEmailExists() {
        UserInsertDTO dto = createInsertDTO();

        mockConstraintViolation();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(new User());

        Assertions.assertFalse(insertValidator.isValid(dto, context));
        Mockito.verify(violationBuilder).addPropertyNode("email");
    }

    @Test
    void insertValidatorShouldReturnFalseWhenTelephoneExists() {
        UserInsertDTO dto = createInsertDTO();

        mockConstraintViolation();
        Mockito.when(repository.findByTelephone("11999999999")).thenReturn(new User());

        Assertions.assertFalse(insertValidator.isValid(dto, context));
        Mockito.verify(violationBuilder).addPropertyNode("telephone");
    }

    @Test
    void insertValidatorShouldReturnFalseWhenEmailAndTelephoneExist() {
        UserInsertDTO dto = createInsertDTO();

        mockConstraintViolation();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(new User());
        Mockito.when(repository.findByTelephone("11999999999")).thenReturn(new User());

        Assertions.assertFalse(insertValidator.isValid(dto, context));
        Mockito.verify(context, Mockito.times(2)).disableDefaultConstraintViolation();
    }

    @Test
    void updateValidatorShouldReturnTrueWhenEmailAndTelephoneAreAvailable() {
        UserUpdateDTO dto = createUpdateDTO();

        mockUriId(1L);

        Assertions.assertTrue(updateValidator.isValid(dto, context));
    }

    @Test
    void updateValidatorShouldReturnTrueWhenEmailAndTelephoneBelongToSameUser() {
        UserUpdateDTO dto = createUpdateDTO();
        User sameUser = new User();
        sameUser.setId(1L);

        mockUriId(1L);
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(sameUser);
        Mockito.when(repository.findByTelephone("11999999999")).thenReturn(sameUser);

        Assertions.assertTrue(updateValidator.isValid(dto, context));
    }

    @Test
    void updateValidatorShouldReturnFalseWhenEmailBelongsToAnotherUser() {
        UserUpdateDTO dto = createUpdateDTO();
        User otherUser = new User();
        otherUser.setId(2L);

        mockUriId(1L);
        mockConstraintViolation();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(otherUser);

        Assertions.assertFalse(updateValidator.isValid(dto, context));
        Mockito.verify(violationBuilder).addPropertyNode("email");
    }

    @Test
    void updateValidatorShouldReturnFalseWhenTelephoneBelongsToAnotherUser() {
        UserUpdateDTO dto = createUpdateDTO();
        User otherUser = new User();
        otherUser.setId(2L);

        mockUriId(1L);
        mockConstraintViolation();
        Mockito.when(repository.findByTelephone("11999999999")).thenReturn(otherUser);

        Assertions.assertFalse(updateValidator.isValid(dto, context));
        Mockito.verify(violationBuilder).addPropertyNode("telephone");
    }

    @Test
    void updateValidatorShouldReturnFalseWhenEmailAndTelephoneBelongToAnotherUser() {
        UserUpdateDTO dto = createUpdateDTO();
        User otherUser = new User();
        otherUser.setId(2L);

        mockUriId(1L);
        mockConstraintViolation();
        Mockito.when(repository.findByEmail("usuario@email.com")).thenReturn(otherUser);
        Mockito.when(repository.findByTelephone("11999999999")).thenReturn(otherUser);

        Assertions.assertFalse(updateValidator.isValid(dto, context));
        Mockito.verify(context, Mockito.times(2)).disableDefaultConstraintViolation();
    }

    private UserInsertDTO createInsertDTO() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setEmail("usuario@email.com");
        dto.setTelephone("11999999999");
        return dto;
    }

    private UserUpdateDTO createUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("usuario@email.com");
        dto.setTelephone("11999999999");
        return dto;
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
