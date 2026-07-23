package com.locadora_rdt_backend.tests.modules.rental.service;

import com.locadora_rdt_backend.modules.rentals.rental.dto.RentalDTO;
import com.locadora_rdt_backend.modules.rentals.rental.model.Rental;
import com.locadora_rdt_backend.modules.rentals.rental.model.RentalItem;
import com.locadora_rdt_backend.modules.rentals.rental.service.RentalFinancialCalculator;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.model.RentalType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

class RentalFinancialCalculatorTests {

    @Test
    void fillLateFeeShouldChargeItemValueForEachOverdueDay() {
        Clock clock = Clock.fixed(Instant.parse("2026-07-20T12:00:00Z"), ZoneOffset.UTC);
        RentalFinancialCalculator calculator = new RentalFinancialCalculator(clock);

        Rental rental = new Rental();
        rental.setExpectedReturnDate(Instant.parse("2026-07-17T12:00:00Z"));
        rental.setTotalAmount(new BigDecimal("50.00"));

        RentalItem item = new RentalItem();
        item.setUnitPrice(new BigDecimal("10.00"));
        item.setQuantity(1);

        RentalDTO dto = new RentalDTO();
        calculator.fillLateFee(rental, List.of(item), dto);

        Assertions.assertEquals(3L, dto.getOverdueDays());
        Assertions.assertEquals(new BigDecimal("10.00"), dto.getLateFeePerDay());
        Assertions.assertEquals(new BigDecimal("30.00"), dto.getCalculatedLateFee());
        Assertions.assertEquals(new BigDecimal("80.00"), dto.getTotalWithLateFee());
    }

    @Test
    void fillLateFeeShouldUseDailySubtotalForOldRentalWithoutItems() {
        Clock clock = Clock.fixed(Instant.parse("2026-07-20T12:00:00Z"), ZoneOffset.UTC);
        RentalFinancialCalculator calculator = new RentalFinancialCalculator(clock);

        RentalType rentalType = new RentalType();
        rentalType.setDays(3);

        Rental rental = new Rental();
        rental.setRentalType(rentalType);
        rental.setExpectedReturnDate(Instant.parse("2026-07-18T12:00:00Z"));
        rental.setSubtotal(new BigDecimal("30.00"));
        rental.setTotalAmount(new BigDecimal("35.00"));

        RentalDTO dto = new RentalDTO();
        calculator.fillLateFee(rental, List.of(), dto);

        Assertions.assertEquals(new BigDecimal("10.00"), dto.getLateFeePerDay());
        Assertions.assertEquals(new BigDecimal("20.00"), dto.getCalculatedLateFee());
        Assertions.assertEquals(new BigDecimal("55.00"), dto.getTotalWithLateFee());
    }
}
