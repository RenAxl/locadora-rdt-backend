package com.locadora_rdt_backend.tests.infrastructure;

import com.locadora_rdt_backend.common.error.StandardError;
import com.locadora_rdt_backend.common.error.ValidationError;
import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.common.handler.ResourceExceptionHandler;
import com.locadora_rdt_backend.infrastructure.mail.service.LoggingEmailService;
import com.locadora_rdt_backend.infrastructure.mail.service.SmtpEmailService;
import com.locadora_rdt_backend.infrastructure.mail.template.ActivationEmailTemplateService;
import com.locadora_rdt_backend.infrastructure.mail.template.PasswordResetEmailTemplateService;
import com.locadora_rdt_backend.infrastructure.security.SpringSecurityAuthenticationFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Method;

class InfrastructureCoverageTests {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void emailTemplatesShouldEscapeNamesAndIncludeLinkAndExpiration() {
        ActivationEmailTemplateService activationTemplate = new ActivationEmailTemplateService();
        PasswordResetEmailTemplateService resetTemplate = new PasswordResetEmailTemplateService();

        String activationHtml = activationTemplate.buildTemplate("<Renan & 'Admin'>", "http://localhost/activate", 30);
        String activationHtmlWithBlankName = activationTemplate.buildTemplate(null, "http://localhost/activate", 30);
        String resetHtml = resetTemplate.buildTemplate("<Renan & 'Admin'>", "http://localhost/reset", 15);
        String resetHtmlWithBlankName = resetTemplate.buildTemplate(null, "http://localhost/reset", 15);

        Assertions.assertTrue(activationHtml.contains("&lt;Renan &amp; &#39;Admin&#39;&gt;"));
        Assertions.assertTrue(activationHtmlWithBlankName.contains("<b></b>"));
        Assertions.assertTrue(activationHtml.contains("http://localhost/activate"));
        Assertions.assertTrue(activationHtml.contains("30 minutos"));
        Assertions.assertTrue(resetHtml.contains("&lt;Renan &amp; &#39;Admin&#39;&gt;"));
        Assertions.assertTrue(resetHtmlWithBlankName.contains("<b></b>"));
        Assertions.assertTrue(resetHtml.contains("http://localhost/reset"));
        Assertions.assertTrue(resetHtml.contains("15 minutos"));
    }

    @Test
    void smtpEmailServiceShouldSendPlainAndHtmlEmail() {
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        SmtpEmailService service = new SmtpEmailService(mailSender);
        ReflectionTestUtils.setField(service, "from", "noreply@locadora.com");

        service.sendEmail("user@locadora.com", "Subject", "Body");
        service.sendHtmlEmail("user@locadora.com", "HTML", "<b>Body</b>");

        ArgumentCaptor<SimpleMailMessage> plainCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender).send(plainCaptor.capture());
        Mockito.verify(mailSender).send(mimeMessage);

        SimpleMailMessage plainMessage = plainCaptor.getValue();
        Assertions.assertEquals("noreply@locadora.com", plainMessage.getFrom());
        Assertions.assertArrayEquals(new String[]{"user@locadora.com"}, plainMessage.getTo());
        Assertions.assertEquals("Subject", plainMessage.getSubject());
        Assertions.assertEquals("Body", plainMessage.getText());
    }

    @Test
    void loggingEmailServiceShouldAcceptPlainAndHtmlEmailWithoutSending() {
        LoggingEmailService service = new LoggingEmailService();

        Assertions.assertDoesNotThrow(() -> service.sendEmail("user@locadora.com", "Subject", "Body"));
        Assertions.assertDoesNotThrow(() -> service.sendHtmlEmail("user@locadora.com", "HTML", "<b>Body</b>"));
    }

    @Test
    void smtpEmailServiceShouldWrapMessagingExceptionWhenHtmlEmailFails() throws Exception {
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        Mockito.doThrow(new javax.mail.MessagingException("invalid from"))
                .when(mimeMessage).setFrom(Mockito.any(InternetAddress.class));
        SmtpEmailService service = new SmtpEmailService(mailSender);
        ReflectionTestUtils.setField(service, "from", "noreply@locadora.com");

        Assertions.assertThrows(MailSendException.class,
                () -> service.sendHtmlEmail("user@locadora.com", "HTML", "<b>Body</b>"));
    }

    @Test
    void authenticationFacadeShouldReturnSystemWhenAuthenticationIsMissingInvalidOrBlank() {
        SpringSecurityAuthenticationFacade facade = new SpringSecurityAuthenticationFacade();

        Assertions.assertEquals("SYSTEM", facade.getAuthenticatedUsername());

        TestingAuthenticationToken unauthenticated = new TestingAuthenticationToken("admin", "password");
        unauthenticated.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(unauthenticated);

        Assertions.assertEquals("SYSTEM", facade.getAuthenticatedUsername());

        TestingAuthenticationToken blankUser = new TestingAuthenticationToken(" ", "password");
        blankUser.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(blankUser);

        Assertions.assertEquals("SYSTEM", facade.getAuthenticatedUsername());
    }

    @Test
    void authenticationFacadeShouldReturnAuthenticatedUsername() {
        SpringSecurityAuthenticationFacade facade = new SpringSecurityAuthenticationFacade();
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("admin", "password");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Assertions.assertEquals("admin", facade.getAuthenticatedUsername());
    }

    @Test
    void handlerShouldMapDomainExceptionsToResponses() {
        ResourceExceptionHandler handler = new ResourceExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/resource");

        assertStandardError(handler.entityNotFound(new ResourceNotFoundException("not found"), request),
                HttpStatus.NOT_FOUND, "Erro de backend: Resource not found", "not found");
        assertStandardError(handler.illegalArgument(new IllegalArgumentException("invalid"), request),
                HttpStatus.BAD_REQUEST, "Erro de backend: Illegal argument", "invalid");
        assertStandardError(handler.runtime(new RuntimeException("unexpected"), request),
                HttpStatus.INTERNAL_SERVER_ERROR, "Erro de backend: Unexpected error", "unexpected");
        assertStandardError(handler.fileException(new FileException("file error"), request),
                HttpStatus.BAD_REQUEST, "Erro de arquivo", "file error");
        assertStandardError(handler.database(new DatabaseException("database error"), request),
                HttpStatus.BAD_REQUEST, "Database exception", "database error");
    }

    @Test
    void handlerShouldMapValidationErrorsToResponse() throws Exception {
        ResourceExceptionHandler handler = new ResourceExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/resource");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dto");
        bindingResult.addError(new FieldError("dto", "name", "must not be blank"));
        Method method = SampleController.class.getDeclaredMethod("handle", String.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                new org.springframework.core.MethodParameter(method, 0),
                bindingResult
        );

        ResponseEntity<ValidationError> response = handler.validation(exception, request);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(422, response.getBody().getStatus());
        Assertions.assertEquals("Erro de backend: Validation exception", response.getBody().getError());
        Assertions.assertEquals("/api/resource", response.getBody().getPath());
        Assertions.assertEquals(1, response.getBody().getErrors().size());
        Assertions.assertEquals("name", response.getBody().getErrors().get(0).getFieldName());
        Assertions.assertEquals("must not be blank", response.getBody().getErrors().get(0).getMessage());
    }

    private void assertStandardError(
            ResponseEntity<StandardError> response,
            HttpStatus status,
            String error,
            String message
    ) {
        Assertions.assertEquals(status, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getTimestamp());
        Assertions.assertEquals(status.value(), response.getBody().getStatus());
        Assertions.assertEquals(error, response.getBody().getError());
        Assertions.assertEquals(message, response.getBody().getMessage());
        Assertions.assertEquals("/api/resource", response.getBody().getPath());
    }

    private static class SampleController {
        void handle(String value) {
            // Method exists only to create a MethodParameter for validation tests.
        }
    }
}
