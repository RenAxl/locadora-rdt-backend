package com.locadora_rdt_backend.infrastructure.whatsapp.service;

public interface WhatsAppService {
    void sendDocument(String phone, byte[] document, String fileName, String caption);
}
