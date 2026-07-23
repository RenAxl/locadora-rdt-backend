package com.locadora_rdt_backend.modules.identity.customeraccount.listener;

import com.locadora_rdt_backend.infrastructure.mail.service.EmailService;
import com.locadora_rdt_backend.infrastructure.mail.template.ActivationEmailTemplateService;
import com.locadora_rdt_backend.modules.identity.customeraccount.event.CustomerAccountActivationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CustomerAccountEmailListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerAccountEmailListener.class);
    private final EmailService emailService;
    private final ActivationEmailTemplateService templateService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.activation.token-minutes:30}")
    private long tokenMinutes;

    public CustomerAccountEmailListener(EmailService emailService,
                                        ActivationEmailTemplateService templateService) {
        this.emailService = emailService;
        this.templateService = templateService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendActivationEmail(CustomerAccountActivationEvent event) {
        try {
            String link = UriComponentsBuilder.fromHttpUrl(frontendBaseUrl)
                    .path("/customer-account/create-password")
                    .queryParam("token", event.getToken())
                    .toUriString();
            String html = templateService.buildTemplate(event.getName(), link, tokenMinutes);
            emailService.sendHtmlEmail(event.getEmail(), "Crie sua senha - Locadora RDT", html);
        } catch (RuntimeException e) {
            LOGGER.error("Não foi possível enviar o e-mail de ativação para {}.", event.getEmail(), e);
        }
    }
}
