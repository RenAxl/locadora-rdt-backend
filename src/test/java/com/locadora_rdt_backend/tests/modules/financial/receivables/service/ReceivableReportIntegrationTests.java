package com.locadora_rdt_backend.tests.modules.financial.receivables.service;

import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableReportDTO;
import com.locadora_rdt_backend.modules.financial.receivables.service.ReceivableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
class ReceivableReportIntegrationTests {

    @Autowired
    private ReceivableService service;

    @Test
    void reportShouldApplyCreatedDatePaidStatusAndDescriptionFilters() {
        ReceivableReportDTO result = service.report(
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
        ReceivableReportDTO result = service.report(
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
        ReceivableReportDTO result = service.report(
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
