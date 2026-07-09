package com.locadora_rdt_backend.modules.inventory.items.controller;

import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemInsertDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemUpdateDTO;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.inventory.items.service.ItemService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/inventory/items")
public class ItemController {

    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/png";

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<ItemDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);

        Page<ItemDTO> list = service.findAllPaged(name.trim(), pageRequest);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailsDTO> findById(@PathVariable Long id) {
        ItemDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ItemDTO> insert(@Valid @RequestBody ItemInsertDTO dto) {
        ItemDTO result = service.insert(dto);

        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateDTO dto
    ) {
        ItemDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        service.updateImage(id, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Item entity = service.findEntityById(id);

        return BinaryResponseBuilder.media(entity.getImage(), DEFAULT_IMAGE_CONTENT_TYPE);
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

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> changeActive(
            @PathVariable Long id,
            @RequestBody boolean active
    ) {
        service.changeActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
