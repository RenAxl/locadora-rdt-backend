package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableReportDTO;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants.*;

@Service
public class ReceivableReportService {
    private final ReceivableRepository repository;
    private final ReceivableFilterNormalizer filterNormalizer;
    private final ReceivableFinancialCalculator financialCalculator;

    public ReceivableReportService(ReceivableRepository repository, ReceivableFilterNormalizer filterNormalizer,
                                   ReceivableFinancialCalculator financialCalculator) {
        this.repository = repository;
        this.filterNormalizer = filterNormalizer;
        this.financialCalculator = financialCalculator;
    }

    @Transactional(readOnly = true)
    public ReceivableReportDTO report(String description, LocalDate startDate, LocalDate endDate,
                                      String status, String dateType) {
        ReceivableFilterDTO filters = new ReceivableFilterDTO();
        filters.setSearch(description);
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setStatus(status);
        filters.setPeriodType(dateType);
        ReceivableFilterDTO normalized = filterNormalizer.normalize(filters);
        List<Receivable> items = repository.findWithFilters(normalized.getSearch(), date(normalized.getStartDate()),
                date(normalized.getEndDate()), normalized.getStartDate() != null, normalized.getEndDate() != null,
                normalized.getStatus(), normalized.getPeriodType(), id(normalized.getCustomerId()),
                id(normalized.getPaymentMethodId()), id(normalized.getPaymentFrequencyId()),
                amount(normalized.getMinimumAmount()), amount(normalized.getMaximumAmount()),
                normalized.getOrderBy(), normalized.getDirection(), Pageable.unpaged()).getContent();
        BigDecimal total = sum(items);
        List<Receivable> paidItems = new ArrayList<>();
        for (Receivable item : items) {
            if (Boolean.TRUE.equals(item.getPaid())) {
                paidItems.add(item);
            }
        }
        BigDecimal paid = sum(paidItems);
        return new ReceivableReportDTO((long) items.size(), total, paid, total.subtract(paid));
    }

    private BigDecimal sum(List<Receivable> items) {
        BigDecimal total = ZERO;
        for (Receivable item : items) {
            total = total.add(financialCalculator.valueOrZero(item.getAmount()));
        }
        return total;
    }

    private Long id(Long value) { return value == null ? FILTER_ID_DISABLED : value; }
    private BigDecimal amount(BigDecimal value) { return value == null ? FILTER_AMOUNT_DISABLED : value; }
    private LocalDate date(LocalDate value) { return value == null ? FILTER_DATE_DISABLED : value; }
}
