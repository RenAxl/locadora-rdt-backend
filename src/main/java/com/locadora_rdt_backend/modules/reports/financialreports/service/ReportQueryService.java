package com.locadora_rdt_backend.modules.reports.financialreports.service;

import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.reports.financialreports.dto.ReportFilterDTO;
import com.locadora_rdt_backend.modules.reports.financialreports.repository.ReportPayableRepository;
import com.locadora_rdt_backend.modules.reports.financialreports.repository.ReportReceivableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.amountFilterOrDisabled;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.dateFilterOrDisabled;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.idFilterOrDisabled;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.normalizeCode;
import static com.locadora_rdt_backend.shared.reports.ReportTableSupport.trimToNull;

@Service
public class ReportQueryService {

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
        normalized.setStatus(normalizeCode(normalized.getStatus(), "ALL"));
        normalized.setPeriodType(normalizeCode(normalized.getPeriodType(), "DUE_DATE"));
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

}
