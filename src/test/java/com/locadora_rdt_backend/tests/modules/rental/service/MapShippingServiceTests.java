package com.locadora_rdt_backend.tests.modules.rental.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locadora_rdt_backend.modules.rentals.rental.dto.ShippingCalculationDTO;
import com.locadora_rdt_backend.modules.rentals.rental.dto.ShippingPriceDTO;
import com.locadora_rdt_backend.modules.rentals.rental.service.MapShippingService;
import com.locadora_rdt_backend.modules.settings.systemsettings.model.Address;
import com.locadora_rdt_backend.modules.settings.systemsettings.model.SystemSetting;
import com.locadora_rdt_backend.modules.settings.systemsettings.service.SystemSettingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;

@ExtendWith(MockitoExtension.class)
class MapShippingServiceTests {
    @Mock private RestTemplate restTemplate;
    @Mock private SystemSettingService systemSettingService;

    private MapShippingService service;
    private ShippingCalculationDTO calculation;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = new MapShippingService(restTemplate, systemSettingService);
        ReflectionTestUtils.setField(service, "baseUrl", "https://api.geoapify.com");
        ReflectionTestUtils.setField(service, "apiKey", "chave-teste");
        calculation = createCalculation();
        Mockito.lenient().when(systemSettingService.findCurrentEntity()).thenReturn(createSetting());
    }

    @Test
    void calculateShouldCostFiveReaisUpToFiveKm() throws Exception {
        mockMapResponses(5000);
        ShippingPriceDTO result = service.calculate(calculation);
        Assertions.assertEquals(new BigDecimal("5.00"), result.getPrice());
        Assertions.assertTrue(result.isDeliveryAvailable());
    }

    @Test
    void calculateShouldCostTenReaisUpToTenKm() throws Exception {
        mockMapResponses(10000);
        ShippingPriceDTO result = service.calculate(calculation);
        Assertions.assertEquals(new BigDecimal("10.00"), result.getPrice());
    }

    @Test
    void calculateShouldCostTwentyReaisUpToTwentyKm() throws Exception {
        mockMapResponses(20000);
        ShippingPriceDTO result = service.calculate(calculation);
        Assertions.assertEquals(new BigDecimal("20.00"), result.getPrice());
    }

    @Test
    void calculateShouldCostThirtyReaisUpToThirtyKm() throws Exception {
        mockMapResponses(30000);
        ShippingPriceDTO result = service.calculate(calculation);
        Assertions.assertEquals(new BigDecimal("30.00"), result.getPrice());
    }

    @Test
    void calculateShouldCostFortyReaisUpToFortyKm() throws Exception {
        mockMapResponses(40000);
        ShippingPriceDTO result = service.calculate(calculation);
        Assertions.assertEquals(new BigDecimal("40.00"), result.getPrice());
        Assertions.assertEquals(0, new BigDecimal("40.00").compareTo(result.getDistanceKm()));
    }

    @Test
    void calculateShouldNotDeliverAboveFortyKm() throws Exception {
        mockMapResponses(40001);
        ShippingPriceDTO result = service.calculate(calculation);
        Assertions.assertFalse(result.isDeliveryAvailable());
        Assertions.assertEquals(new BigDecimal("0.00"), result.getPrice());
    }

    @Test
    void calculateShouldThrowWhenApiKeyIsMissing() {
        ReflectionTestUtils.setField(service, "apiKey", "");
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calculate(calculation));
        Mockito.verifyNoInteractions(restTemplate);
    }

    @Test
    void calculateShouldThrowWhenStoreAddressIsIncomplete() {
        SystemSetting setting = new SystemSetting();
        setting.setAddress(new Address());
        Mockito.when(systemSettingService.findCurrentEntity()).thenReturn(setting);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calculate(calculation));
        Mockito.verifyNoInteractions(restTemplate);
    }

    @Test
    void calculateShouldThrowWhenMapApiIsUnavailable() {
        Mockito.when(restTemplate.getForObject(Mockito.any(URI.class), Mockito.eq(JsonNode.class)))
                .thenThrow(new RestClientException("indisponível"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calculate(calculation));
    }

    private void mockMapResponses(double distanceMeters) throws Exception {
        JsonNode origin = mapper.readTree("{\"results\":[{\"lat\":-23.55,\"lon\":-46.63}]}");
        JsonNode destination = mapper.readTree("{\"results\":[{\"lat\":-23.56,\"lon\":-46.64}]}");
        JsonNode route = mapper.readTree("{\"features\":[{\"properties\":{\"distance\":"
                + distanceMeters + "}}]}");
        Mockito.when(restTemplate.getForObject(Mockito.any(URI.class), Mockito.eq(JsonNode.class)))
                .thenReturn(origin, destination, route);
    }

    private ShippingCalculationDTO createCalculation() {
        ShippingCalculationDTO dto = new ShippingCalculationDTO();
        dto.setStreet("Rua do Cliente");
        dto.setNumber("20");
        dto.setNeighborhood("Centro");
        dto.setCity("São Paulo");
        dto.setState("SP");
        dto.setZipCode("01001-001");
        return dto;
    }

    private SystemSetting createSetting() {
        Address address = new Address();
        address.setStreet("Rua da Locadora");
        address.setNumber("10");
        address.setNeighborhood("Centro");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setZipCode("01000-000");
        SystemSetting setting = new SystemSetting();
        setting.setAddress(address);
        return setting;
    }
}
