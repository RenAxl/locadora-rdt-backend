package com.locadora_rdt_backend.modules.financial.receivables.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReceivablePaymentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Informe o valor da baixa")
    @DecimalMin(value = "0.01", message = "Valor de baixa deve ser maior que zero")
    private BigDecimal paymentAmount;

    private LocalDate paymentDate;
    private Long paymentMethodId;
    private BigDecimal subtotal;
    private BigDecimal fee;
    private BigDecimal lateInterest;
    private BigDecimal lateFee;
    private BigDecimal discount;

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public BigDecimal getLateInterest() {
        return lateInterest;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public void setLateInterest(BigDecimal lateInterest) {
        this.lateInterest = lateInterest;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}

