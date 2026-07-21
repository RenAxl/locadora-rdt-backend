package com.locadora_rdt_backend.tests.modules.rental.service;

import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.rental.model.Rental;
import com.locadora_rdt_backend.modules.rental.model.RentalItem;
import com.locadora_rdt_backend.modules.rental.service.RentalDocumentPdfService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;

class RentalDocumentPdfServiceTests {
    private RentalDocumentPdfService service;
    private Rental rental;
    private RentalItem rentalItem;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-07-21T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        service = new RentalDocumentPdfService(clock);

        Customer customer = new Customer();
        customer.setName("Renan Duarte");
        customer.setCpf("11111111111");

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName("PIX");

        rental = new Rental(11L, "LOC-20260721144335071");
        rental.setCustomer(customer);
        rental.setStartDate(Instant.parse("2026-07-19T14:43:00Z"));
        rental.setActualReturnDate(Instant.parse("2026-07-21T14:44:00Z"));
        rental.setTotalAmount(new BigDecimal("20.00"));
        rental.setLateFee(new BigDecimal("2.00"));
        rental.setDiscount(new BigDecimal("1.00"));
        rental.setPaymentMethod(paymentMethod);
        rental.setUpdatedBy("renandt30@gmail.com");

        rentalItem = new RentalItem();
        rentalItem.setItem(new Item(1L, "Controle DualSense"));
        rentalItem.setQuantity(1);
        rentalItem.setUnitPrice(new BigDecimal("20.00"));
        rentalItem.setSubtotal(new BigDecimal("20.00"));
    }

    @Test
    void buildReceiptPdfShouldGeneratePdf() throws Exception {
        byte[] pdf = service.buildReceiptPdf(rental, Collections.singletonList(rentalItem));

        Assertions.assertTrue(pdf.length > 0);
        Assertions.assertEquals("%PDF", new String(pdf, 0, 4));
    }

    @Test
    void buildFiscalCouponPdfShouldGeneratePdf() throws Exception {
        byte[] pdf = service.buildFiscalCouponPdf(rental, Collections.singletonList(rentalItem));

        Assertions.assertTrue(pdf.length > 0);
        Assertions.assertEquals("%PDF", new String(pdf, 0, 4));
    }

    @Test
    void buildDocumentsShouldAcceptOptionalValues() throws Exception {
        Rental simpleRental = new Rental(12L, null);
        simpleRental.setTotalAmount(null);

        byte[] receipt = service.buildReceiptPdf(simpleRental, null);
        byte[] coupon = service.buildFiscalCouponPdf(simpleRental, Collections.emptyList());

        Assertions.assertTrue(receipt.length > 0);
        Assertions.assertTrue(coupon.length > 0);
    }
}
