package com.locadora_rdt_backend.infrastructure.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "false")
public class LoggingEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEmailService.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        LOGGER.info("Email desabilitado. to={}, subject={}, body={}", to, subject, body);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        LOGGER.info("Email HTML desabilitado. to={}, subject={}, html={}", to, subject, htmlBody);
    }
}
