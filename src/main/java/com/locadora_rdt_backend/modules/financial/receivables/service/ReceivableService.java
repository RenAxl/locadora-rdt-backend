package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableReportDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableUpdateDTO;
import com.locadora_rdt_backend.modules.rentals.rental.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants.*;

public interface ReceivableService {

    default Page<ReceivableDTO> findAllPaged(String description, PageRequest pageRequest) {
        ReceivableFilterDTO filters = new ReceivableFilterDTO();
        filters.setSearch(description);
        filters.setStatus(STATUS_ALL);
        filters.setPeriodType(PERIOD_DUE_DATE);
        filters.setOrderBy(ORDER_BY_DUE_DATE);
        filters.setDirection(DIRECTION_ASC);
        return findAllPaged(filters, pageRequest);
    }

    Page<ReceivableDTO> findAllPaged(ReceivableFilterDTO filters, PageRequest pageRequest);

    ReceivableDetailsDTO findById(Long id);

    ReceivableDTO insert(ReceivableInsertDTO dto);

    void createFromRental(Rental rental);

    ReceivableDTO update(Long id, ReceivableUpdateDTO dto);

    void delete(Long id);

    ReceivableDTO pay(Long id, ReceivablePaymentDTO dto);

    List<ReceivableDTO> installment(Long id, ReceivableInstallmentDTO dto);

    ReceivableReportDTO report(String description, LocalDate startDate, LocalDate endDate, String status, String dateType);

    byte[] receipt(Long id);

    byte[] fiscalCoupon(Long id);
}
