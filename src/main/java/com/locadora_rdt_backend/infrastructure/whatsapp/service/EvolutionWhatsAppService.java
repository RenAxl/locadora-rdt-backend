package com.locadora_rdt_backend.infrastructure.whatsapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "app.whatsapp.enabled", havingValue = "true")
public class EvolutionWhatsAppService implements WhatsAppService {
    private final RestTemplate restTemplate;

    @Value("${app.whatsapp.base-url}")
    private String baseUrl;

    @Value("${app.whatsapp.api-key}")
    private String apiKey;

    @Value("${app.whatsapp.instance-name}")
    private String instanceName;

    public EvolutionWhatsAppService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendDocument(String phone, byte[] document, String fileName, String caption) {
        String number = normalizePhone(phone);
        String url = removeLastSlash(baseUrl) + "/message/sendMedia/" + instanceName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", apiKey);

        String documentBase64 = Base64.getEncoder().encodeToString(document);
        Map<String, Object> body = new HashMap<>();
        body.put("number", number);
        body.put("mediatype", "document");
        body.put("mimetype", "application/pdf");
        body.put("media", documentBase64);
        body.put("caption", caption);
        body.put("fileName", fileName);

        restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("O cliente não possui telefone cadastrado.");
        }

        String number = phone.replaceAll("\\D", "");
        if (number.length() == 10 || number.length() == 11) {
            number = "55" + number;
        }
        if (number.length() < 12 || number.length() > 13) {
            throw new IllegalArgumentException("O telefone do cliente é inválido para envio pelo WhatsApp.");
        }
        return number;
    }

    private String removeLastSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
