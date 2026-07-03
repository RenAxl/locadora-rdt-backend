package com.locadora_rdt_backend.tests.modules.receivables;

import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFileViewDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableReportDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.service.ReceivableService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

class ReceivableFinancialCoverageTests {

    @Test
    void receivableReportDTOShouldSupportDefaultConstructorAndSetters() {
        ReceivableReportDTO dto = new ReceivableReportDTO();
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
    void receivableFileViewDTOShouldSupportDefaultConstructor() {
        ReceivableFileViewDTO dto = new ReceivableFileViewDTO();

        Assertions.assertNull(dto.getFileName());
        Assertions.assertNull(dto.getContentType());
        Assertions.assertNull(dto.getData());
    }

    @Test
    void receivableMapperShouldHandleNullRelationsAndTrimFields() {
        ReceivableMapper mapper = new ReceivableMapper();
        Receivable entity = new Receivable();
        entity.setId(1L);
        entity.setDescription("Conta");
        entity.setAmount(new BigDecimal("45.90"));

        ReceivableDTO dto = mapper.toDTO(entity);

        Assertions.assertEquals(1L, dto.getId());
        Assertions.assertNull(dto.getCustomerId());
        Assertions.assertNull(dto.getPaymentMethodId());
        Assertions.assertNull(dto.getPaymentFrequencyId());
        Assertions.assertNull(dto.getCreatedById());
        Assertions.assertNull(dto.getPaidById());

        ReceivableInsertDTO insertDTO = new ReceivableInsertDTO();
        insertDTO.setDescription("  Locacao  ");
        insertDTO.setAmount(new BigDecimal("10.00"));
        insertDTO.setNote("  ");
        insertDTO.setFileName(" comprovante.pdf ");
        insertDTO.setPaymentDate(LocalDate.of(2026, 7, 1));

        Receivable inserted = mapper.toEntity(insertDTO);

        Assertions.assertEquals("Locacao", inserted.getDescription());
        Assertions.assertNull(inserted.getNote());
        Assertions.assertEquals("comprovante.pdf", inserted.getFileName());
        Assertions.assertTrue(inserted.getPaid());
        Assertions.assertEquals(BigDecimal.ZERO, inserted.getRemainingBalance());
    }

    @Test
    void receivableModelShouldFillDefaultsAndSyncStatus() {
        Receivable receivable = new Receivable();
        receivable.setPaid(null);
        receivable.setResidual(null);
        receivable.setCanceled(null);

        receivable.prePersist();

        Assertions.assertFalse(receivable.getPaid());
        Assertions.assertFalse(receivable.getResidual());
        Assertions.assertFalse(receivable.getCanceled());
        Assertions.assertEquals("PENDING", receivable.getStatus());

        receivable.setPaid(true);
        receivable.preUpdate();
        Assertions.assertEquals("PAID", receivable.getStatus());

        receivable.setCanceled(true);
        receivable.preUpdate();
        Assertions.assertEquals("CANCELED", receivable.getStatus());
        Assertions.assertNotNull(receivable.getUpdatedAt());
    }

    @Test
    void receivableModelEqualsShouldHandleNullAndDifferentIds() {
        Receivable first = new Receivable();
        Receivable second = new Receivable();

        Assertions.assertEquals(first, first);
        Assertions.assertEquals(first, second);
        Assertions.assertNotEquals(first, null);
        Assertions.assertNotEquals(first, "receivable");

        first.setId(1L);
        Assertions.assertNotEquals(first, second);

        second.setId(2L);
        Assertions.assertNotEquals(first, second);

        second.setId(1L);
        Assertions.assertEquals(first, second);
        Assertions.assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void receivableServiceDefaultFindAllPagedShouldBuildDefaultFilters() {
        ReceivableDTO dto = new ReceivableDTO();
        dto.setId(1L);
        PageRequest pageRequest = PageRequest.of(0, 10);
        CapturingReceivableService service = new CapturingReceivableService(new PageImpl<>(List.of(dto)));

        Page<ReceivableDTO> result = service.findAllPaged("Movie", pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals("Movie", service.capturedFilters.getSearch());
        Assertions.assertEquals("ALL", service.capturedFilters.getStatus());
        Assertions.assertEquals("DUE_DATE", service.capturedFilters.getPeriodType());
        Assertions.assertEquals("dueDate", service.capturedFilters.getOrderBy());
        Assertions.assertEquals("ASC", service.capturedFilters.getDirection());
        Assertions.assertSame(pageRequest, service.capturedPageRequest);
    }

    private static class CapturingReceivableService implements ReceivableService {
        private final Page<ReceivableDTO> page;
        private ReceivableFilterDTO capturedFilters;
        private PageRequest capturedPageRequest;

        private CapturingReceivableService(Page<ReceivableDTO> page) {
            this.page = page;
        }

        @Override
        public Page<ReceivableDTO> findAllPaged(ReceivableFilterDTO filters, PageRequest pageRequest) {
            this.capturedFilters = filters;
            this.capturedPageRequest = pageRequest;
            return page;
        }

        @Override
        public ReceivableDetailsDTO findById(Long id) {
            return null;
        }

        @Override
        public ReceivableDTO insert(ReceivableInsertDTO dto) {
            return null;
        }

        @Override
        public ReceivableDTO update(Long id, ReceivableUpdateDTO dto) {
            return null;
        }

        @Override
        public void delete(Long id) {
        }

        @Override
        public ReceivableDTO pay(Long id, ReceivablePaymentDTO dto) {
            return null;
        }

        @Override
        public List<ReceivableDTO> installment(Long id, ReceivableInstallmentDTO dto) {
            return List.of();
        }

        @Override
        public ReceivableReportDTO report(String description, LocalDate startDate, LocalDate endDate, String status, String dateType) {
            return null;
        }

        @Override
        public byte[] receipt(Long id) {
            return new byte[0];
        }

        @Override
        public byte[] fiscalCoupon(Long id) {
            return new byte[0];
        }
    }
}
