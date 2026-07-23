package com.locadora_rdt_backend.modules.rentals.rental.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.locadora_rdt_backend.modules.rentals.rental.dto.ShippingCalculationDTO;
import com.locadora_rdt_backend.modules.rentals.rental.dto.ShippingPriceDTO;
import com.locadora_rdt_backend.modules.settings.systemsettings.model.Address;
import com.locadora_rdt_backend.modules.settings.systemsettings.model.SystemSetting;
import com.locadora_rdt_backend.modules.settings.systemsettings.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class MapShippingService {
    private static final BigDecimal FIVE_KM = new BigDecimal("5.00");
    private static final BigDecimal TEN_KM = new BigDecimal("10.00");
    private static final BigDecimal TWENTY_KM = new BigDecimal("20.00");
    private static final BigDecimal THIRTY_KM = new BigDecimal("30.00");
    private static final BigDecimal FORTY_KM = new BigDecimal("40.00");

    private final RestTemplate restTemplate;
    private final SystemSettingService systemSettingService;

    @Value("${app.geoapify.base-url:https://api.geoapify.com}")
    private String baseUrl;

    @Value("${app.geoapify.api-key:}")
    private String apiKey;

    public MapShippingService(RestTemplate restTemplate, SystemSettingService systemSettingService) {
        this.restTemplate = restTemplate;
        this.systemSettingService = systemSettingService;
    }

    public ShippingPriceDTO calculate(ShippingCalculationDTO dto) {
        validateConfiguration();

        try {
            String originAddress = findOriginAddress();
            String destinationAddress = formatAddress(dto);
            Coordinates origin = findCoordinates(originAddress, "locadora");
            Coordinates destination = findCoordinates(destinationAddress, "cliente");
            BigDecimal distanceKm = findDrivingDistance(origin, destination);
            return createShippingPrice(distanceKm);
        } catch (RestClientException exception) {
            throw new IllegalArgumentException("Não foi possível calcular a distância da entrega.");
        }
    }

    private Coordinates findCoordinates(String address, String addressOwner) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v1/geocode/search")
                .queryParam("text", address)
                .queryParam("format", "json")
                .queryParam("filter", "countrycode:br")
                .queryParam("limit", 1)
                .queryParam("apiKey", apiKey)
                .build()
                .encode()
                .toUri();

        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);
        JsonNode result = response == null ? null : response.path("results").path(0);
        if (result == null || result.isMissingNode()) {
            throw new IllegalArgumentException("Não foi possível localizar o endereço da " + addressOwner + ".");
        }
        return new Coordinates(result.path("lat").asDouble(), result.path("lon").asDouble());
    }

    private BigDecimal findDrivingDistance(Coordinates origin, Coordinates destination) {
        String waypoints = origin.latitude + "," + origin.longitude + "|"
                + destination.latitude + "," + destination.longitude;
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v1/routing")
                .queryParam("waypoints", waypoints)
                .queryParam("mode", "drive")
                .queryParam("apiKey", apiKey)
                .build()
                .encode()
                .toUri();

        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);
        JsonNode distance = response == null ? null
                : response.path("features").path(0).path("properties").path("distance");
        if (distance == null || distance.isMissingNode() || !distance.isNumber()) {
            throw new IllegalArgumentException("Não foi possível encontrar uma rota para entrega.");
        }

        return BigDecimal.valueOf(distance.asDouble())
                .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
    }

    private ShippingPriceDTO createShippingPrice(BigDecimal distanceKm) {
        if (distanceKm.compareTo(FIVE_KM) <= 0) {
            return new ShippingPriceDTO(new BigDecimal("5.00"), distanceKm, true);
        }
        if (distanceKm.compareTo(TEN_KM) <= 0) {
            return new ShippingPriceDTO(new BigDecimal("10.00"), distanceKm, true);
        }
        if (distanceKm.compareTo(TWENTY_KM) <= 0) {
            return new ShippingPriceDTO(new BigDecimal("20.00"), distanceKm, true);
        }
        if (distanceKm.compareTo(THIRTY_KM) <= 0) {
            return new ShippingPriceDTO(new BigDecimal("30.00"), distanceKm, true);
        }
        if (distanceKm.compareTo(FORTY_KM) <= 0) {
            return new ShippingPriceDTO(new BigDecimal("40.00"), distanceKm, true);
        }
        return new ShippingPriceDTO(BigDecimal.ZERO.setScale(2), distanceKm, false);
    }

    private String findOriginAddress() {
        SystemSetting setting = systemSettingService.findCurrentEntity();
        if (setting.getAddress() == null) {
            throw new IllegalArgumentException("Cadastre o endereço da locadora nas configurações do sistema.");
        }
        return formatAddress(setting.getAddress());
    }

    private String formatAddress(ShippingCalculationDTO dto) {
        return joinAddress(dto.getStreet(), dto.getNumber(), dto.getNeighborhood(),
                dto.getCity(), dto.getState(), dto.getZipCode(), "Brasil");
    }

    private String formatAddress(Address address) {
        if (isEmpty(address.getStreet()) || isEmpty(address.getNumber()) || isEmpty(address.getNeighborhood())
                || isEmpty(address.getCity()) || isEmpty(address.getState()) || isEmpty(address.getZipCode())) {
            throw new IllegalArgumentException("Complete o endereço da locadora nas configurações do sistema.");
        }
        return joinAddress(address.getStreet(), address.getNumber(), address.getNeighborhood(),
                address.getCity(), address.getState(), address.getZipCode(), "Brasil");
    }

    private void validateConfiguration() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("A chave da API de mapas ainda não foi configurada.");
        }
    }

    private boolean isEmpty(String value) { return value == null || value.trim().isEmpty(); }

    private String joinAddress(String... parts) {
        return Arrays.stream(parts)
                .filter(part -> !isEmpty(part))
                .collect(Collectors.joining(", "));
    }

    private static class Coordinates {
        private final double latitude;
        private final double longitude;

        private Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
