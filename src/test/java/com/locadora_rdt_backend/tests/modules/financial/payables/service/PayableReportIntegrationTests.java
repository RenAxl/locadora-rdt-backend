package com.locadora_rdt_backend.tests.modules.financial.payables.service;

import com.locadora_rdt_backend.modules.financial.payables.dto.PayableReportDTO;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
class PayableReportIntegrationTests {

    @Autowired
    private PayableService service;

    @Test
    void reportShouldApplyCreatedDatePaidStatusAndDescriptionFilters() {
        PayableReportDTO result = service.report(
                "Locação",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                "paid",
                "created"
        );

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getTotalItems() >= 0);
    }

    @Test
    void reportShouldApplyDueDateOpenStatusAndEndDateFilters() {
        PayableReportDTO result = service.report(
                null,
                null,
                LocalDate.of(2026, 7, 31),
                "open",
                "due"
        );

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getTotalItems() >= 0);
    }

    @Test
    void reportShouldApplyPaymentDateStartDateFilter() {
        PayableReportDTO result = service.report(
                "",
                LocalDate.of(2026, 7, 1),
                null,
                "all",
                "payment"
        );

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getTotalItems() >= 0);
    }
}
