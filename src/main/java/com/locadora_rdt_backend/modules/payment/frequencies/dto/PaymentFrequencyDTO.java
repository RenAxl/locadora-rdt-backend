package com.locadora_rdt_backend.modules.payment.frequencies.dto;

import java.io.Serializable;

public class PaymentFrequencyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String frequency;
    private Integer days;

    public PaymentFrequencyDTO() {
    }

    public PaymentFrequencyDTO(Long id, String frequency, Integer days) {
        this.id = id;
        this.frequency = frequency;
        this.days = days;
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
