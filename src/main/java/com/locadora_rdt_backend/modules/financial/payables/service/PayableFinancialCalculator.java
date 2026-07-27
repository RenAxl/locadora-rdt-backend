package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.modules.settings.financialsettings.model.FinancialSetting;
import com.locadora_rdt_backend.modules.settings.financialsettings.repository.FinancialSettingRepository;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class PayableFinancialCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final FinancialSettingRepository financialSettingRepository;
    private final Clock clock;

    public PayableFinancialCalculator(FinancialSettingRepository financialSettingRepository, Clock clock) {
        this.financialSettingRepository = financialSettingRepository;
        this.clock = clock;
    }

    public BigDecimal getCurrentPaymentLimit(Payable entity, PayablePaymentDTO dto) {
        BigDecimal limit = getOpenAmount(entity);
        limit = limit.add(valueOrZero(dto.getFee()));
        limit = limit.add(valueOrZero(dto.getLateInterest()));
        limit = limit.add(valueOrZero(dto.getLateFee()));
        limit = limit.subtract(valueOrZero(dto.getDiscount()));

        return limit;
    }

    public BigDecimal getOpenAmount(Payable entity) {
        BigDecimal amount = valueOrZero(entity.getAmount());
        BigDecimal paidAmount;

        if (hasPaymentRecord(entity)) {
            paidAmount = valueOrZero(entity.getSubtotal());
        } else {
            paidAmount = ZERO;
        }

        if (amount.compareTo(ZERO) > 0 && paidAmount.compareTo(amount) >= 0) {
            return ZERO;
        }

        if (paidAmount.compareTo(ZERO) > 0 && paidAmount.compareTo(amount) < 0) {
            return amount.subtract(paidAmount);
        }

        BigDecimal remaining = entity.getRemainingBalance();

        if (remaining != null && remaining.compareTo(ZERO) > 0 && remaining.compareTo(amount) < 0) {
            return remaining;
        }

        return amount;
    }

    public void fillLateCharges(Payable entity, PayableDTO dto) {
        BigDecimal amount = valueOrZero(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal openAmount = getOpenAmount(entity).setScale(2, RoundingMode.HALF_UP);
        if (Boolean.TRUE.equals(entity.getPaid())) {
            dto.setCurrentAmountWithLateCharges(amount);
        } else {
            dto.setCurrentAmountWithLateCharges(openAmount);
        }

        dto.setOverdueDays(0L);
        dto.setCalculatedLateInterest(ZERO.setScale(2, RoundingMode.HALF_UP));
        dto.setCalculatedLateFee(ZERO.setScale(2, RoundingMode.HALF_UP));

        if (!isOverdueOpenPayable(entity)) {
            return;
        }

        long overdueDays = ChronoUnit.DAYS.between(entity.getDueDate(), today());
        FinancialSetting setting = financialSettingRepository
                .findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY)
                .orElseGet(FinancialSetting::new);
        BigDecimal lateFee = percentageOf(openAmount, setting.getDefaultLateFeePercent());
        BigDecimal lateInterest = percentageOf(openAmount, setting.getDefaultLateInterestPercent())
                .multiply(BigDecimal.valueOf(overdueDays))
                .setScale(2, RoundingMode.HALF_UP);

        dto.setOverdueDays(overdueDays);
        dto.setCalculatedLateInterest(lateInterest);
        dto.setCalculatedLateFee(lateFee);
        BigDecimal amountWithLateCharges = openAmount.add(lateInterest);
        amountWithLateCharges = amountWithLateCharges.add(lateFee);
        amountWithLateCharges = amountWithLateCharges.setScale(2, RoundingMode.HALF_UP);

        dto.setCurrentAmountWithLateCharges(amountWithLateCharges);
    }

    public BigDecimal valueOrZero(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }

        return value;
    }

    private boolean isOverdueOpenPayable(Payable entity) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            return false;
        }

        if (Boolean.TRUE.equals(entity.getCanceled())) {
            return false;
        }

        if (entity.getDueDate() == null) {
            return false;
        }

        if (!entity.getDueDate().isBefore(today())) {
            return false;
        }

        return getOpenAmount(entity).compareTo(ZERO) > 0;
    }

    private BigDecimal percentageOf(BigDecimal amount, BigDecimal percent) {
        BigDecimal value = amount.multiply(valueOrZero(percent));
        return value.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private boolean hasPaymentRecord(Payable entity) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            return true;
        }

        return entity.getPaymentDate() != null;
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
