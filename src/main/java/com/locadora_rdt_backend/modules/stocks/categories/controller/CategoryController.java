package com.locadora_rdt_backend.modules.stocks.categories.controller;

import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryInsertDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryUpdateDTO;
import com.locadora_rdt_backend.modules.stocks.categories.model.Category;
import com.locadora_rdt_backend.modules.stocks.categories.service.CategoryService;
import com.locadora_rdt_backend.shared.web.BinaryResponseBuilder;
import com.locadora_rdt_backend.shared.web.ControllerResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static com.locadora_rdt_backend.modules.stocks.categories.constants.CategoryAuthorizationExpressions.*;

@RestController
@RequestMapping("/rental/categories")
public class CategoryController {

    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/png";

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PreAuthorize(CATEGORIES_READ + " or " + CUSTOMERS_CATEGORIES_READ)
    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = ControllerResponseBuilder.pageRequest(page, linesPerPage, direction, orderBy);

        Page<CategoryDTO> list = service.findAllPaged(name.trim(), pageRequest);

        return ResponseEntity.ok(list);
    }

    @PreAuthorize(CATEGORIES_READ)
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDetailsDTO> findById(@PathVariable Long id) {
        CategoryDetailsDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize(CATEGORIES_WRITE)
    @PostMapping
    public ResponseEntity<CategoryDTO> insert(@Valid @RequestBody CategoryInsertDTO dto) {
        CategoryDTO result = service.insert(dto);

        return ControllerResponseBuilder.created(result.getId(), result);
    }

    @PreAuthorize(CATEGORIES_WRITE)
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO dto
    ) {
        CategoryDTO result = service.update(id, dto);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize(CATEGORIES_WRITE)
    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        service.updateImage(id, file);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(CATEGORIES_READ)
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Category entity = service.findEntityById(id);

        return BinaryResponseBuilder.media(entity.getImage(), DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @PreAuthorize(CATEGORIES_DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(CATEGORIES_DELETE)
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll(@RequestBody List<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(CATEGORIES_WRITE)
    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> changeActive(
            @PathVariable Long id,
            @RequestBody boolean active
    ) {
        service.changeActiveStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
