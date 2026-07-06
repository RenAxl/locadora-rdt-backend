package com.locadora_rdt_backend.tests.modules.payables;

import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFileViewDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInsertDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableReportDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payables.mapper.PayableMapper;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

class PayableFinancialCoverageTests {

    @Test
    void payableReportDTOShouldSupportDefaultConstructorAndSetters() {
        PayableReportDTO dto = new PayableReportDTO();
        dto.setTotalItems(2L);
        dto.setTotalAmount(new BigDecimal("30.00"));
        dto.setPaidAmount(new BigDecimal("10.00"));
        dto.setOpenAmount(new BigDecimal("20.00"));

        Assertions.assertEquals(2L, dto.getTotalItems());
        Assertions.assertEquals(new BigDecimal("30.00"), dto.getTotalAmount());
        Assertions.assertEquals(new BigDecimal("10.00"), dto.getPaidAmount());
        Assertions.assertEquals(new BigDecimal("20.00"), dto.getOpenAmount());
    }

    @Test
    void payableFileViewDTOShouldSupportDefaultConstructor() {
        PayableFileViewDTO dto = new PayableFileViewDTO();

        Assertions.assertNull(dto.getFileName());
        Assertions.assertNull(dto.getContentType());
        Assertions.assertNull(dto.getData());
    }

    @Test
    void payableMapperShouldHandleNullRelationsAndTrimFields() {
        PayableMapper mapper = new PayableMapper();
        Payable entity = new Payable();
        entity.setId(1L);
        entity.setDescription("Conta");
        entity.setAmount(new BigDecimal("45.90"));

        PayableDTO dto = mapper.toDTO(entity);

        Assertions.assertEquals(1L, dto.getId());
        Assertions.assertNull(dto.getSupplierId());
        Assertions.assertNull(dto.getPaymentMethodId());
        Assertions.assertNull(dto.getPaymentFrequencyId());
        Assertions.assertNull(dto.getCreatedById());
        Assertions.assertNull(dto.getPaidById());

        PayableInsertDTO insertDTO = new PayableInsertDTO();
        insertDTO.setDescription("  Locacao  ");
        insertDTO.setAmount(new BigDecimal("10.00"));
        insertDTO.setNote("  ");
        insertDTO.setFileName(" comprovante.pdf ");
        insertDTO.setPaymentDate(LocalDate.of(2026, 7, 1));

        Payable inserted = mapper.toEntity(insertDTO);

        Assertions.assertEquals("Locacao", inserted.getDescription());
        Assertions.assertNull(inserted.getNote());
        Assertions.assertEquals("comprovante.pdf", inserted.getFileName());
        Assertions.assertTrue(inserted.getPaid());
        Assertions.assertEquals(BigDecimal.ZERO, inserted.getRemainingBalance());
    }

    @Test
    void payableModelShouldFillDefaultsAndSyncStatus() {
        Payable payable = new Payable();
        payable.setPaid(null);
        payable.setResidual(null);
        payable.setCanceled(null);

        payable.prePersist();

        Assertions.assertFalse(payable.getPaid());
        Assertions.assertFalse(payable.getResidual());
        Assertions.assertFalse(payable.getCanceled());
        Assertions.assertEquals("PENDING", payable.getStatus());

        payable.setPaid(true);
        payable.preUpdate();
        Assertions.assertEquals("PAID", payable.getStatus());

        payable.setCanceled(true);
        payable.preUpdate();
        Assertions.assertEquals("CANCELED", payable.getStatus());
        Assertions.assertNotNull(payable.getUpdatedAt());
    }

    @Test
    void payableModelEqualsShouldHandleNullAndDifferentIds() {
        Payable first = new Payable();
        Payable second = new Payable();

        Assertions.assertEquals(first, first);
        Assertions.assertEquals(first, second);
        Assertions.assertNotEquals(first, null);
        Assertions.assertNotEquals(first, "payable");

        first.setId(1L);
        Assertions.assertNotEquals(first, second);

        second.setId(2L);
        Assertions.assertNotEquals(first, second);

        second.setId(1L);
        Assertions.assertEquals(first, second);
        Assertions.assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void payableServiceDefaultFindAllPagedShouldBuildDefaultFilters() {
        PayableDTO dto = new PayableDTO();
        dto.setId(1L);
        PageRequest pageRequest = PageRequest.of(0, 10);
        CapturingPayableService service = new CapturingPayableService(new PageImpl<>(List.of(dto)));

        Page<PayableDTO> result = service.findAllPaged("Movie", pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals("Movie", service.capturedFilters.getSearch());
        Assertions.assertEquals("ALL", service.capturedFilters.getStatus());
        Assertions.assertEquals("DUE_DATE", service.capturedFilters.getPeriodType());
        Assertions.assertEquals("dueDate", service.capturedFilters.getOrderBy());
        Assertions.assertEquals("ASC", service.capturedFilters.getDirection());
        Assertions.assertSame(pageRequest, service.capturedPageRequest);
    }

    private static class CapturingPayableService implements PayableService {
        private final Page<PayableDTO> page;
        private PayableFilterDTO capturedFilters;
        private PageRequest capturedPageRequest;

        private CapturingPayableService(Page<PayableDTO> page) {
            this.page = page;
        }

        @Override
        public Page<PayableDTO> findAllPaged(PayableFilterDTO filters, PageRequest pageRequest) {
            this.capturedFilters = filters;
            this.capturedPageRequest = pageRequest;
            return page;
        }

        @Override
        public PayableDetailsDTO findById(Long id) {
            return null;
        }

        @Override
        public PayableDTO insert(PayableInsertDTO dto) {
            return null;
        }

        @Override
        public PayableDTO update(Long id, PayableUpdateDTO dto) {
            return null;
        }

        @Override
        public void delete(Long id) {
        }

        @Override
        public PayableDTO pay(Long id, PayablePaymentDTO dto) {
            return null;
        }

        @Override
        public List<PayableDTO> installment(Long id, PayableInstallmentDTO dto) {
            return List.of();
        }

        @Override
        public PayableReportDTO report(String description, LocalDate startDate, LocalDate endDate, String status, String dateType) {
            return null;
        }

    }
}
