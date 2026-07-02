package com.locadora_rdt_backend.modules.financial.payment.frequencies.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class PaymentFrequencyUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Frequência é obrigatória")
    @Size(min = 3, max = 60, message = "Frequência deve ter entre 3 e 60 caracteres")
    private String frequency;

    @NotNull(message = "Dias é obrigatório")
    @Min(value = 0, message = "Dias deve ser maior ou igual a zero")
    private Integer days;

    public PaymentFrequencyUpdateDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getFrequency() {
        return frequency;
    }

    public Integer getDays() {
        return days;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
