package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.modules.financial.payables.constants.PayableConstants;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableReportDTO;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayableReportService {

    private final PayableRepository repository;
    private final PayableFilterNormalizer filterNormalizer;
    private final PayableFinancialCalculator financialCalculator;

    public PayableReportService(
            PayableRepository repository,
            PayableFilterNormalizer filterNormalizer,
            PayableFinancialCalculator financialCalculator
    ) {
        this.repository = repository;
        this.filterNormalizer = filterNormalizer;
        this.financialCalculator = financialCalculator;
    }

    @Transactional(readOnly = true)
    public PayableReportDTO report(
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String dateType
    ) {
        PayableFilterDTO reportFilters = new PayableFilterDTO();
        reportFilters.setSearch(description);
        reportFilters.setStartDate(startDate);
        reportFilters.setEndDate(endDate);
        reportFilters.setStatus(status);
        reportFilters.setPeriodType(dateType);

        PayableFilterDTO normalized = filterNormalizer.normalize(reportFilters);
        List<Payable> items = repository.findWithFilters(
                normalized.getSearch(),
                dateFilterOrDisabled(normalized.getStartDate()),
                dateFilterOrDisabled(normalized.getEndDate()),
                normalized.getStartDate() != null,
                normalized.getEndDate() != null,
                normalized.getStatus(),
                normalized.getPeriodType(),
                idFilterOrDisabled(normalized.getSupplierId()),
                idFilterOrDisabled(normalized.getEmployeeId()),
                idFilterOrDisabled(normalized.getPaymentMethodId()),
                idFilterOrDisabled(normalized.getPaymentFrequencyId()),
                amountFilterOrDisabled(normalized.getMinimumAmount()),
                amountFilterOrDisabled(normalized.getMaximumAmount()),
                normalized.getOrderBy(),
                normalized.getDirection(),
                Pageable.unpaged()
        ).getContent();
        BigDecimal total = sum(items);
        List<Payable> paidItems = new ArrayList<>();

        for (Payable item : items) {
            if (Boolean.TRUE.equals(item.getPaid())) {
                paidItems.add(item);
            }
        }

        BigDecimal paid = sum(paidItems);
        BigDecimal open = total.subtract(paid);

        return new PayableReportDTO((long) items.size(), total, paid, open);
    }

    private BigDecimal sum(List<Payable> items) {
        BigDecimal total = PayableConstants.ZERO;

        for (Payable item : items) {
            total = total.add(financialCalculator.valueOrZero(item.getAmount()));
        }

        return total;
    }

    private Long idFilterOrDisabled(Long id) {
        return id == null ? PayableConstants.FILTER_ID_DISABLED : id;
    }

    private BigDecimal amountFilterOrDisabled(BigDecimal amount) {
        return amount == null ? PayableConstants.FILTER_AMOUNT_DISABLED : amount;
    }

    private LocalDate dateFilterOrDisabled(LocalDate date) {
        return date == null ? PayableConstants.FILTER_DATE_DISABLED : date;
    }
}
