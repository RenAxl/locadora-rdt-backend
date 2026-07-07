package com.locadora_rdt_backend.tests.infrastructure.mail.service;

import com.locadora_rdt_backend.infrastructure.mail.service.LoggingEmailService;
import com.locadora_rdt_backend.infrastructure.mail.service.SmtpEmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

    @Mock
    private JavaMailSender mailSender;

    private SmtpEmailService smtpEmailService;

    @BeforeEach
    void setUp() {
        smtpEmailService = new SmtpEmailService(mailSender);
        ReflectionTestUtils.setField(smtpEmailService, "from", "noreply@locadora.com");
    }

    @Test
    void loggingEmailServiceShouldAcceptPlainAndHtmlEmail() {
        LoggingEmailService service = new LoggingEmailService();

        Assertions.assertDoesNotThrow(() -> service.sendEmail("to@email.com", "Assunto", "Texto"));
        Assertions.assertDoesNotThrow(() -> service.sendHtmlEmail("to@email.com", "Assunto", "<b>Texto</b>"));
    }

    @Test
    void smtpEmailServiceShouldSendPlainEmail() {
        smtpEmailService.sendEmail("to@email.com", "Assunto", "Texto");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        Assertions.assertEquals("noreply@locadora.com", message.getFrom());
        Assertions.assertArrayEquals(new String[]{"to@email.com"}, message.getTo());
        Assertions.assertEquals("Assunto", message.getSubject());
        Assertions.assertEquals("Texto", message.getText());
    }

    @Test
    void smtpEmailServiceShouldSendHtmlEmail() throws Exception {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        Mockito.when(mailSender.createMimeMessage()).thenReturn(message);

        smtpEmailService.sendHtmlEmail("to@email.com", "Assunto", "<b>Texto</b>");

        Mockito.verify(mailSender).send(message);
        Assertions.assertEquals("Assunto", message.getSubject());
        Assertions.assertEquals("noreply@locadora.com", message.getFrom()[0].toString());
        Assertions.assertEquals("to@email.com", message.getAllRecipients()[0].toString());
    }

    @Test
    void smtpEmailServiceShouldWrapMessagingException() {
        Mockito.when(mailSender.createMimeMessage()).thenReturn(new FailingMimeMessage());

        Assertions.assertThrows(MailSendException.class,
                () -> smtpEmailService.sendHtmlEmail("to@email.com", "Assunto", "<b>Texto</b>"));
    }

    private static class FailingMimeMessage extends MimeMessage {

        FailingMimeMessage() {
            super(Session.getInstance(new Properties()));
        }

        @Override
        public void setFrom(javax.mail.Address address) throws javax.mail.MessagingException {
            throw new javax.mail.MessagingException("Falha");
        }
    }
}
