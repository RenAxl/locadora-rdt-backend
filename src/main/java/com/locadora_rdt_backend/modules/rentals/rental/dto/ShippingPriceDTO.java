package com.locadora_rdt_backend.modules.rentals.rental.dto;

import java.math.BigDecimal;

public class ShippingPriceDTO {
    private BigDecimal price;
    private BigDecimal distanceKm;
    private boolean deliveryAvailable;

    public ShippingPriceDTO() {}

    public ShippingPriceDTO(BigDecimal price, BigDecimal distanceKm, boolean deliveryAvailable) {
        this.price = price;
        this.distanceKm = distanceKm;
        this.deliveryAvailable = deliveryAvailable;
    }

    public BigDecimal getPrice() { return price; }
    public BigDecimal getDistanceKm() { return distanceKm; }
    public boolean isDeliveryAvailable() { return deliveryAvailable; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }
    public void setDeliveryAvailable(boolean deliveryAvailable) { this.deliveryAvailable = deliveryAvailable; }
}
