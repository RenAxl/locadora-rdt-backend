package com.locadora_rdt_backend.modules.financial.payables.controller;

import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInsertDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableReportDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/payables")
public class PayableController {

    private final PayableService service;

    public PayableController(PayableService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<PayableDTO>> findAllPaged(
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "dateType", defaultValue = "due") String dateType,
            @RequestParam(value = "periodType", required = false) String periodType,
            @RequestParam(value = "supplierId", required = false) Long supplierId,
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "paymentMethodId", required = false) Long paymentMethodId,
            @RequestParam(value = "paymentFrequencyId", required = false) Long paymentFrequencyId,
            @RequestParam(value = "minimumAmount", required = false) BigDecimal minimumAmount,
            @RequestParam(value = "maximumAmount", required = false) BigDecimal maximumAmount,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "dueDate") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage);
        PayableFilterDTO filters = new PayableFilterDTO();
        filters.setSearch(search == null || search.trim().isEmpty() ? description.trim() : search.trim());
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setStatus(status);
        filters.setPeriodType(periodType == null || periodType.trim().isEmpty() ? dateType : periodType);
        filters.setSupplierId(supplierId);
        filters.setEmployeeId(employeeId);
        filters.setPaymentMethodId(paymentMethodId);
        filters.setPaymentFrequencyId(paymentFrequencyId);
        filters.setMinimumAmount(minimumAmount);
        filters.setMaximumAmount(maximumAmount);
        filters.setOrderBy(orderBy);
        filters.setDirection(direction);

        Page<PayableDTO> list = service.findAllPaged(filters, pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayableDetailsDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<PayableDTO> insert(@Valid @RequestBody PayableInsertDTO dto) {
        PayableDTO result = service.insert(dto);
        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PayableDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PayableUpdateDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<PayableDTO> pay(
            @PathVariable Long id,
            @Valid @RequestBody PayablePaymentDTO dto
    ) {
        return ResponseEntity.ok(service.pay(id, dto));
    }

    @PostMapping("/{id}/installments")
    public ResponseEntity<List<PayableDTO>> installment(
            @PathVariable Long id,
            @Valid @RequestBody PayableInstallmentDTO dto
    ) {
        return ResponseEntity.ok(service.installment(id, dto));
    }

    @GetMapping("/report")
    public ResponseEntity<PayableReportDTO> report(
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "dateType", defaultValue = "due") String dateType
    ) {
        return ResponseEntity.ok(service.report(description, startDate, endDate, status, dateType));
    }

}
