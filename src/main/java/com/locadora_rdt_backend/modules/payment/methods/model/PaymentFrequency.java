package com.locadora_rdt_backend.modules.payment.methods.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tb_payment_frequency")
public class PaymentFrequency implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String frequency;

    @Column(nullable = false)
    private Integer days;

    public PaymentFrequency() {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        PaymentFrequency other = (PaymentFrequency) obj;

        if (id == null) {
            return other.id == null;
        }

        return id.equals(other.id);
    }
}
