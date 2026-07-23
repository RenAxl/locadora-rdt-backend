package com.locadora_rdt_backend.modules.rentals.rental.service;

import com.locadora_rdt_backend.modules.rentals.rental.dto.RentalDTO;
import com.locadora_rdt_backend.modules.rentals.rental.model.Rental;
import com.locadora_rdt_backend.modules.rentals.rental.model.RentalItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class RentalFinancialCalculator {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final Clock clock;

    public RentalFinancialCalculator(Clock clock) {
        this.clock = clock;
    }

    public void fillLateFee(Rental rental, List<RentalItem> items, RentalDTO dto) {
        dto.setOverdueDays(0L);
        dto.setLateFeePerDay(ZERO);
        dto.setCalculatedLateFee(ZERO);
        dto.setTotalWithLateFee(valueOrZero(rental.getTotalAmount()));

        if (rental.getExpectedReturnDate() == null) {
            return;
        }

        LocalDate expectedDate = LocalDate.ofInstant(rental.getExpectedReturnDate(), clock.getZone());
        LocalDate returnDate = LocalDate.now(clock);
        if (rental.getActualReturnDate() != null) {
            returnDate = LocalDate.ofInstant(rental.getActualReturnDate(), clock.getZone());
        }

        if (!expectedDate.isBefore(returnDate)) {
            return;
        }

        long overdueDays = ChronoUnit.DAYS.between(expectedDate, returnDate);
        BigDecimal totalAmount = valueOrZero(rental.getTotalAmount());
        BigDecimal lateFeePerDay = calculateItemsDailyValue(items);
        if (lateFeePerDay.compareTo(ZERO) == 0) {
            lateFeePerDay = calculateLegacyDailyValue(rental);
        }
        BigDecimal calculatedLateFee = lateFeePerDay.multiply(BigDecimal.valueOf(overdueDays));
        calculatedLateFee = calculatedLateFee.setScale(2, RoundingMode.HALF_UP);

        dto.setOverdueDays(overdueDays);
        dto.setLateFeePerDay(lateFeePerDay);
        dto.setCalculatedLateFee(calculatedLateFee);
        dto.setTotalWithLateFee(totalAmount.add(calculatedLateFee).setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal calculateItemsDailyValue(List<RentalItem> items) {
        BigDecimal dailyValue = ZERO;
        if (items == null) {
            return dailyValue;
        }

        for (RentalItem item : items) {
            BigDecimal unitPrice = valueOrZero(item.getUnitPrice());
            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            dailyValue = dailyValue.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        }
        return dailyValue.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLegacyDailyValue(Rental rental) {
        if (rental.getRentalType() == null || rental.getRentalType().getDays() == null
                || rental.getRentalType().getDays() <= 0) {
            return ZERO;
        }

        BigDecimal subtotal = valueOrZero(rental.getSubtotal());
        return subtotal.divide(
                BigDecimal.valueOf(rental.getRentalType().getDays()),
                2,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
