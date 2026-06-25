package com.locadora_rdt_backend.modules.payment.methods.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentMethodInsertDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 60, message = "Nome deve ter entre 3 e 60 caracteres")
    private String name;

    @DecimalMin(value = "0.0", message = "Taxa deve ser maior ou igual a zero")
    @Digits(integer = 10, fraction = 2, message = "Taxa inválida")
    private BigDecimal fee;

    public PaymentMethodInsertDTO() {
        // Required by frameworks and serializers.
    }

    public String getName() {
        return name;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}
