package com.locadora_rdt_backend.modules.financial.receivables.controller;

import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableReportDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.receivables.service.ReceivableService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/receivables")
public class ReceivableController {

    private final ReceivableService service;

    public ReceivableController(ReceivableService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<ReceivableDTO>> findAllPaged(
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "dateType", defaultValue = "due") String dateType,
            @RequestParam(value = "periodType", required = false) String periodType,
            @RequestParam(value = "customerId", required = false) Long customerId,
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
        ReceivableFilterDTO filters = new ReceivableFilterDTO();
        filters.setSearch(search == null || search.trim().isEmpty() ? description.trim() : search.trim());
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setStatus(status);
        filters.setPeriodType(periodType == null || periodType.trim().isEmpty() ? dateType : periodType);
        filters.setCustomerId(customerId);
        filters.setPaymentMethodId(paymentMethodId);
        filters.setPaymentFrequencyId(paymentFrequencyId);
        filters.setMinimumAmount(minimumAmount);
        filters.setMaximumAmount(maximumAmount);
        filters.setOrderBy(orderBy);
        filters.setDirection(direction);

        Page<ReceivableDTO> list = service.findAllPaged(filters, pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceivableDetailsDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReceivableDTO> insert(@Valid @RequestBody ReceivableInsertDTO dto) {
        ReceivableDTO result = service.insert(dto);
        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceivableDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ReceivableUpdateDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<ReceivableDTO> pay(
            @PathVariable Long id,
            @Valid @RequestBody ReceivablePaymentDTO dto
    ) {
        return ResponseEntity.ok(service.pay(id, dto));
    }

    @PostMapping("/{id}/installments")
    public ResponseEntity<List<ReceivableDTO>> installment(
            @PathVariable Long id,
            @Valid @RequestBody ReceivableInstallmentDTO dto
    ) {
        return ResponseEntity.ok(service.installment(id, dto));
    }

    @GetMapping("/report")
    public ResponseEntity<ReceivableReportDTO> report(
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "dateType", defaultValue = "due") String dateType
    ) {
        return ResponseEntity.ok(service.report(description, startDate, endDate, status, dateType));
    }

    @GetMapping(value = "/{id}/receipt", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> receipt(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=recibo-" + id + ".pdf")
                .body(service.receipt(id));
    }

    @GetMapping(value = "/{id}/fiscal-coupon", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> fiscalCoupon(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=cupom-fiscal-" + id + ".pdf")
                .body(service.fiscalCoupon(id));
    }
}
