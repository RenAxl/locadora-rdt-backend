package com.locadora_rdt_backend.modules.rentals.catalog.controller;

import com.locadora_rdt_backend.modules.rentals.catalog.service.CatalogService;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/png";

    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<ItemDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "8") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);
        Page<ItemDTO> list = service.findAllPaged(name.trim(), categoryId, pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailsDTO> findById(@PathVariable Long id) {
        ItemDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Item entity = service.findEntityById(id);

        return BinaryResponseBuilder.media(entity.getImage(), DEFAULT_IMAGE_CONTENT_TYPE);
    }
}
