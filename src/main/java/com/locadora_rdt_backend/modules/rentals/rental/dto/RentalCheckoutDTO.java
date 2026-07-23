package com.locadora_rdt_backend.modules.rentals.rental.dto;

import javax.validation.constraints.NotNull;

public class RentalCheckoutDTO {
    @NotNull
    private Long paymentMethodId;

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
