package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInsertDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableReportDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

public interface PayableService {

    default Page<PayableDTO> findAllPaged(String description, PageRequest pageRequest) {
        PayableFilterDTO filters = new PayableFilterDTO();
        filters.setSearch(description);
        filters.setStatus("ALL");
        filters.setPeriodType("DUE_DATE");
        filters.setOrderBy("dueDate");
        filters.setDirection("ASC");
        return findAllPaged(filters, pageRequest);
    }

    Page<PayableDTO> findAllPaged(PayableFilterDTO filters, PageRequest pageRequest);

    PayableDetailsDTO findById(Long id);

    PayableDTO insert(PayableInsertDTO dto);

    PayableDTO update(Long id, PayableUpdateDTO dto);

    void delete(Long id);

    PayableDTO pay(Long id, PayablePaymentDTO dto);

    List<PayableDTO> installment(Long id, PayableInstallmentDTO dto);

    PayableReportDTO report(String description, LocalDate startDate, LocalDate endDate, String status, String dateType);
}
