package com.locadora_rdt_backend.modules.rentals.rental.controller;

import com.locadora_rdt_backend.modules.rentals.rental.dto.*;
import com.locadora_rdt_backend.modules.rentals.rental.service.RentalService;
import com.locadora_rdt_backend.modules.rentals.rental.service.MapShippingService;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import com.locadora_rdt_backend.modules.organization.customers.dto.CustomerDTO;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService service;
    private final MapShippingService shippingService;

    public RentalController(RentalService service, MapShippingService shippingService) {
        this.service = service;
        this.shippingService = shippingService;
    }

    @GetMapping
    public ResponseEntity<Page<RentalDTO>> findAll(
            @RequestParam(defaultValue = "") String number,
            @RequestParam(defaultValue = "") String customer,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(required = false) Long rentalTypeId,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer linesPerPage,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "rental_date") String orderBy) {
        PageRequest request = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);
        return ResponseEntity.ok(service.findAllPaged(number, customer, status, rentalTypeId, dateFrom, dateTo, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDetailsDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }

    @GetMapping("/current-customer")
    public ResponseEntity<CustomerDTO> findCurrentCustomer() {
        return ResponseEntity.ok(service.findCurrentCustomer());
    }

    @PostMapping("/shipping/calculate")
    public ResponseEntity<ShippingPriceDTO> calculateShipping(@Valid @RequestBody ShippingCalculationDTO dto) {
        return ResponseEntity.ok(shippingService.calculate(dto));
    }

    @PostMapping
    public ResponseEntity<RentalDTO> insert(@Valid @RequestBody RentalSaveDTO dto) {
        RentalDTO result = service.insert(dto);
        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> update(@PathVariable Long id, @Valid @RequestBody RentalSaveDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<RentalDTO> confirm(@PathVariable Long id) { return ResponseEntity.ok(service.confirm(id)); }

    @PatchMapping("/{id}/start")
    public ResponseEntity<RentalDTO> start(@PathVariable Long id, @Valid @RequestBody RentalCheckoutDTO dto) {
        return ResponseEntity.ok(service.start(id, dto));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RentalDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @GetMapping("/availability/items/{itemId}")
    public ResponseEntity<ItemAvailabilityDTO> findAvailability(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.findAvailability(itemId));
    }

    @GetMapping("/availability/items/{itemId}/units")
    public ResponseEntity<List<ItemUnitDTO>> findAvailableUnits(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.findAvailableUnits(itemId));
    }

    @GetMapping("/availability/items/{itemId}/all-units")
    public ResponseEntity<List<ItemUnitDTO>> findItemUnits(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.findItemUnits(itemId));
    }

    @GetMapping("/{id}/units")
    public ResponseEntity<List<RentalItemUnitDTO>> findRentalUnits(@PathVariable Long id) {
        return ResponseEntity.ok(service.findRentalUnits(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<RentalStatusHistoryDTO>> findHistory(@PathVariable Long id) {
        return ResponseEntity.ok(service.findHistory(id));
    }

    @GetMapping(value = "/{id}/receipt", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> receipt(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=recibo-locacao-" + id + ".pdf")
                .body(service.receipt(id));
    }

    @GetMapping(value = "/{id}/fiscal-coupon", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> fiscalCoupon(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=cupom-fiscal-locacao-" + id + ".pdf")
                .body(service.fiscalCoupon(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
