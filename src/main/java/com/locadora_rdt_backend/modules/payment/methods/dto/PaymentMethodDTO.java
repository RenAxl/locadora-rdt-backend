package com.locadora_rdt_backend.modules.payment.methods.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentMethodDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private BigDecimal fee;

    public PaymentMethodDTO() {
        // Required by frameworks and serializers.
    }

    public PaymentMethodDTO(Long id, String name, BigDecimal fee) {
        this.id = id;
        this.name = name;
        this.fee = fee;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}
