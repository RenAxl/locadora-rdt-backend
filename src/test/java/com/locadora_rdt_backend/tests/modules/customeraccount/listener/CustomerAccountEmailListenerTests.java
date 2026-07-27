package com.locadora_rdt_backend.tests.modules.customeraccount.listener;

import com.locadora_rdt_backend.infrastructure.mail.service.EmailService;
import com.locadora_rdt_backend.infrastructure.mail.template.ActivationEmailTemplateService;
import com.locadora_rdt_backend.modules.identity.customeraccount.event.CustomerAccountActivationEvent;
import com.locadora_rdt_backend.modules.identity.customeraccount.listener.CustomerAccountEmailListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CustomerAccountEmailListenerTests {
    private EmailService emailService;
    private ActivationEmailTemplateService templateService;
    private CustomerAccountEmailListener listener;

    @BeforeEach
    void setUp() {
        emailService = Mockito.mock(EmailService.class);
        templateService = Mockito.mock(ActivationEmailTemplateService.class);
        listener = new CustomerAccountEmailListener(emailService, templateService);
        ReflectionTestUtils.setField(listener, "frontendBaseUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(listener, "tokenMinutes", 30L);
    }

    @Test
    void sendActivationEmailShouldUseCustomerPasswordLink() {
        Mockito.when(templateService.buildTemplate(Mockito.eq("Ana"), Mockito.anyString(), Mockito.eq(30L)))
                .thenReturn("html");

        listener.sendActivationEmail(new CustomerAccountActivationEvent("Ana", "ana@email.com", "abc"));

        Mockito.verify(templateService).buildTemplate(
                "Ana",
                "http://localhost:4200/customer-account/create-password?token=abc",
                30L
        );
        Mockito.verify(emailService).sendHtmlEmail("ana@email.com", "Crie sua senha - Locadora RDT", "html");
    }

    @Test
    void sendActivationEmailShouldNotThrowWhenEmailFails() {
        Mockito.when(templateService.buildTemplate(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong()))
                .thenReturn("html");
        Mockito.doThrow(new IllegalStateException("Falha"))
                .when(emailService).sendHtmlEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        Assertions.assertDoesNotThrow(() -> listener.sendActivationEmail(
                new CustomerAccountActivationEvent("Ana", "ana@email.com", "abc")));
    }
}
