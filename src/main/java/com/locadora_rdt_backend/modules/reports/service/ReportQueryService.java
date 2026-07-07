package com.locadora_rdt_backend.modules.reports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.repository.ReportPayableRepository;
import com.locadora_rdt_backend.modules.reports.repository.ReportReceivableRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportQueryService {

    private static final BigDecimal FILTER_AMOUNT_DISABLED = BigDecimal.valueOf(-1);
    private static final LocalDate FILTER_DATE_DISABLED = LocalDate.of(1970, 1, 1);
    private static final long FILTER_ID_DISABLED = -1L;

    private final ReportReceivableRepository receivableRepository;
    private final ReportPayableRepository payableRepository;

    public ReportQueryService(
            ReportReceivableRepository receivableRepository,
            ReportPayableRepository payableRepository
    ) {
        this.receivableRepository = receivableRepository;
        this.payableRepository = payableRepository;
    }

    public List<Receivable> findReceivables(ReportFilterDTO filters) {
        return receivableRepository.findForReports(
                trimToNull(filters.getSearch()),
                dateFilterOrDisabled(filters.getStartDate()),
                dateFilterOrDisabled(filters.getEndDate()),
                filters.getStartDate() != null,
                filters.getEndDate() != null,
                filters.getStatus(),
                filters.getPeriodType(),
                idFilterOrDisabled(filters.getCustomerId()),
                idFilterOrDisabled(filters.getPaymentMethodId()),
                amountFilterOrDisabled(filters.getMinimumAmount()),
                amountFilterOrDisabled(filters.getMaximumAmount())
        );
    }

    public List<Payable> findPayables(ReportFilterDTO filters) {
        return payableRepository.findForReports(
                trimToNull(filters.getSearch()),
                dateFilterOrDisabled(filters.getStartDate()),
                dateFilterOrDisabled(filters.getEndDate()),
                filters.getStartDate() != null,
                filters.getEndDate() != null,
                filters.getStatus(),
                filters.getPeriodType(),
                idFilterOrDisabled(filters.getSupplierId()),
                idFilterOrDisabled(filters.getEmployeeId()),
                idFilterOrDisabled(filters.getPaymentMethodId()),
                amountFilterOrDisabled(filters.getMinimumAmount()),
                amountFilterOrDisabled(filters.getMaximumAmount())
        );
    }

    public ReportFilterDTO normalize(ReportFilterDTO filters) {
        ReportFilterDTO normalized = filters == null ? new ReportFilterDTO() : filters;

        if (!hasText(normalized.getStatus())) {
            normalized.setStatus("ALL");
        } else {
            normalized.setStatus(normalized.getStatus().trim().replace("-", "_").toUpperCase());
        }

        if (!hasText(normalized.getPeriodType())) {
            normalized.setPeriodType("DUE_DATE");
        } else {
            normalized.setPeriodType(normalized.getPeriodType().trim().replace("-", "_").toUpperCase());
        }

        return normalized;
    }

    public ReportFilterDTO copy(ReportFilterDTO source) {
        ReportFilterDTO copy = new ReportFilterDTO();
        copy.setSearch(source.getSearch());
        copy.setStartDate(source.getStartDate());
        copy.setEndDate(source.getEndDate());
        copy.setStatus(source.getStatus());
        copy.setPeriodType(source.getPeriodType());
        copy.setCustomerId(source.getCustomerId());
        copy.setSupplierId(source.getSupplierId());
        copy.setEmployeeId(source.getEmployeeId());
        copy.setPaymentMethodId(source.getPaymentMethodId());
        copy.setMinimumAmount(source.getMinimumAmount());
        copy.setMaximumAmount(source.getMaximumAmount());
        copy.setYear(source.getYear());
        return copy;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private Long idFilterOrDisabled(Long id) {
        if (id == null || id <= 0) {
            return FILTER_ID_DISABLED;
        }

        return id;
    }

    private BigDecimal amountFilterOrDisabled(BigDecimal amount) {
        if (amount == null) {
            return FILTER_AMOUNT_DISABLED;
        }

        return amount;
    }

    private LocalDate dateFilterOrDisabled(LocalDate date) {
        if (date == null) {
            return FILTER_DATE_DISABLED;
        }

        return date;
    }
}
