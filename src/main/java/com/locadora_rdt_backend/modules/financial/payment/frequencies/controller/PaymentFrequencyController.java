package com.locadora_rdt_backend.modules.financial.payment.frequencies.controller;

import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyInsertDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.service.PaymentFrequencyService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;

@RestController
@RequestMapping("/payment-frequencies")
public class PaymentFrequencyController {

    private final PaymentFrequencyService service;

    public PaymentFrequencyController(PaymentFrequencyService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<PaymentFrequencyDTO>> findAllPaged(
            @RequestParam(value = "frequency", defaultValue = "") String frequency,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "3") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "frequency") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);

        Page<PaymentFrequencyDTO> list = service.findAllPaged(frequency.trim(), pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentFrequencyDetailsDTO> findById(@PathVariable Long id) {
        PaymentFrequencyDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PaymentFrequencyDTO> insert(@Valid @RequestBody PaymentFrequencyInsertDTO dto) {
        PaymentFrequencyDTO result = service.insert(dto);

        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentFrequencyDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PaymentFrequencyUpdateDTO dto
    ) {
        PaymentFrequencyDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll(@RequestBody List<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.noContent().build();
    }
}
