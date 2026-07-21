package com.locadora_rdt_backend.tests.infrastructure.whatsapp.service;

import com.locadora_rdt_backend.infrastructure.whatsapp.service.EvolutionWhatsAppService;
import com.locadora_rdt_backend.infrastructure.whatsapp.service.LoggingWhatsAppService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class EvolutionWhatsAppServiceTests {
    @Mock
    private RestTemplate restTemplate;

    private EvolutionWhatsAppService service;

    @BeforeEach
    void setUp() {
        service = new EvolutionWhatsAppService(restTemplate);
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8081/");
        ReflectionTestUtils.setField(service, "apiKey", "test-key");
        ReflectionTestUtils.setField(service, "instanceName", "locadora-rdt");
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendDocumentShouldSendPdfWithBrazilianPhone() {
        service.sendDocument("(31) 99999-9999", new byte[]{1, 2}, "recibo.pdf", "Recibo");

        ArgumentCaptor<HttpEntity> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate).postForEntity(
                Mockito.eq("http://localhost:8081/message/sendMedia/locadora-rdt"),
                requestCaptor.capture(),
                Mockito.eq(String.class)
        );
        Map<String, Object> body = (Map<String, Object>) requestCaptor.getValue().getBody();
        Assertions.assertEquals("5531999999999", body.get("number"));
        Assertions.assertEquals("document", body.get("mediatype"));
        Assertions.assertEquals("application/pdf", body.get("mimetype"));
        Assertions.assertEquals("AQI=", body.get("media"));
        Assertions.assertEquals("application/json", requestCaptor.getValue().getHeaders().getContentType().toString());
        Assertions.assertEquals("test-key", requestCaptor.getValue().getHeaders().getFirst("apikey"));
    }

    @Test
    void sendDocumentShouldRejectEmptyPhone() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.sendDocument("", new byte[]{1}, "recibo.pdf", "Recibo")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendTextShouldSendMessageWithBrazilianPhone() {
        service.sendText("(31) 99999-9999", "Seu item está a caminho.");

        ArgumentCaptor<HttpEntity> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate).postForEntity(
                Mockito.eq("http://localhost:8081/message/sendText/locadora-rdt"),
                requestCaptor.capture(),
                Mockito.eq(String.class)
        );
        Map<String, Object> body = (Map<String, Object>) requestCaptor.getValue().getBody();
        Assertions.assertEquals("5531999999999", body.get("number"));
        Assertions.assertEquals("Seu item está a caminho.", body.get("text"));
        Assertions.assertEquals("test-key", requestCaptor.getValue().getHeaders().getFirst("apikey"));
    }

    @Test
    void disabledServiceShouldRejectSending() {
        LoggingWhatsAppService disabledService = new LoggingWhatsAppService();

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> disabledService.sendDocument("31999999999", new byte[]{1}, "recibo.pdf", "Recibo")
        );
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> disabledService.sendText("31999999999", "Mensagem")
        );
    }
}
