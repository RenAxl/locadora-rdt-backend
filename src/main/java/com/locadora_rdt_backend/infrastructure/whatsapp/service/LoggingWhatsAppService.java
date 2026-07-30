package com.locadora_rdt_backend.infrastructure.whatsapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.whatsapp.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingWhatsAppService implements WhatsAppService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingWhatsAppService.class);

    @Override
    public void sendText(String phone, String message) {
        LOGGER.info("Envio de mensagem por WhatsApp desabilitado. Telefone: {}", phone);
    }

    @Override
    public void sendDocument(String phone, byte[] document, String fileName, String caption) {
        LOGGER.info("Envio de documento por WhatsApp desabilitado. Telefone: {}, arquivo: {}", phone, fileName);
    }
}
