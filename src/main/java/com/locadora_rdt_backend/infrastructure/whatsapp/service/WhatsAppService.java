package com.locadora_rdt_backend.infrastructure.whatsapp.service;

public interface WhatsAppService {
    void sendText(String phone, String message);

    void sendDocument(String phone, byte[] document, String fileName, String caption);
}
