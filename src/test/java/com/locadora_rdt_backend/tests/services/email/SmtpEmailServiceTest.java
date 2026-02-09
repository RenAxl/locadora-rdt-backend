package com.locadora_rdt_backend.tests.services.email;

import com.locadora_rdt_backend.services.email.SmtpEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SmtpEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private SmtpEmailService service;

    private final String FROM = "test@locadora.com";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // seta o campo private String from; (injetado por @Value no runtime)
        ReflectionTestUtils.setField(service, "from", FROM);
    }

    @Test
    @DisplayName("sendEmail deve montar SimpleMailMessage corretamente e chamar mailSender.send(msg)")
    void sendEmail_shouldSendSimpleMailMessageCorrectly() {
        String to = "cliente@teste.com";
        String subject = "Assunto";
        String body = "Conteúdo do email";

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        service.sendEmail(to, subject, body);

        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals(FROM, msg.getFrom());
        assertArrayEquals(new String[]{to}, msg.getTo());
        assertEquals(subject, msg.getSubject());
        assertEquals(body, msg.getText());
    }

    @Test
    @DisplayName("sendEmail deve propagar RuntimeException se mailSender.send(msg) falhar")
    void sendEmail_shouldPropagateExceptionIfMailSenderFails() {
        String to = "cliente@teste.com";
        String subject = "Assunto";
        String body = "Conteúdo";

        doThrow(new RuntimeException("Falha SMTP")).when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.sendEmail(to, subject, body));

        assertEquals("Falha SMTP", ex.getMessage());
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("sendHtmlEmail deve criar MimeMessage, setar campos e enviar com mailSender.send(message)")
    void sendHtmlEmail_shouldSendMimeMessageCorrectly() {
        String to = "cliente@teste.com";
        String subject = "Bem-vindo!";
        String htmlBody = "<h1>Olá</h1><p>Conta criada</p>";

        // MimeMessage real (não precisa mockar javax.mail)
        MimeMessage mimeMessage = new MimeMessage((Session) null);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        service.sendHtmlEmail(to, subject, htmlBody);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("sendHtmlEmail deve lançar RuntimeException com mensagem padrão se createMimeMessage falhar")
    void sendHtmlEmail_shouldThrowRuntimeExceptionIfCreateMimeMessageFails() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro createMimeMessage"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.sendHtmlEmail("a@a.com", "x", "<b>y</b>"));

        assertEquals("Erro ao enviar email", ex.getMessage());
        assertNotNull(ex.getCause());
        assertEquals("Erro createMimeMessage", ex.getCause().getMessage());

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendHtmlEmail deve lançar RuntimeException se mailSender.send(message) falhar")
    void sendHtmlEmail_shouldThrowRuntimeExceptionIfSendFails() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("Falha ao enviar")).when(mailSender).send(mimeMessage);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.sendHtmlEmail("a@a.com", "x", "<b>y</b>"));

        assertEquals("Erro ao enviar email", ex.getMessage());
        assertNotNull(ex.getCause());
        assertEquals("Falha ao enviar", ex.getCause().getMessage());

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("sendHtmlEmail deve lançar RuntimeException se ocorrer erro ao montar MimeMessageHelper")
    void sendHtmlEmail_shouldThrowRuntimeExceptionIfHelperFails() {
        when(mailSender.createMimeMessage()).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.sendHtmlEmail("a@a.com", "x", "<b>y</b>"));

        assertEquals("Erro ao enviar email", ex.getMessage());
        assertNotNull(ex.getCause());

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
