package com.locadora_rdt_backend.infrastructure.whatsapp.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.whatsapp.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingWhatsAppService implements WhatsAppService {
    @Override
    public void sendDocument(String phone, byte[] document, String fileName, String caption) {
        throw new IllegalStateException("O envio por WhatsApp não está configurado.");
    }
}
