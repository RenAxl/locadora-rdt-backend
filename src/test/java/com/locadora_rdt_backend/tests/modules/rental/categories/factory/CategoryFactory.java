package com.locadora_rdt_backend.tests.modules.rental.categories.factory;

import com.locadora_rdt_backend.modules.rental.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.rental.categories.dto.CategoryDetailsDTO;
import com.locadora_rdt_backend.modules.rental.categories.dto.CategoryInsertDTO;
import com.locadora_rdt_backend.modules.rental.categories.dto.CategoryUpdateDTO;
import com.locadora_rdt_backend.modules.rental.categories.model.Category;

import java.time.Instant;

public class CategoryFactory {

    public static Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Consoles");
        category.setActive(true);
        category.setImage(new byte[]{1});
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());
        category.setCreatedBy("admin");
        category.setUpdatedBy("admin");
        return category;
    }

    public static Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setActive(true);
        category.setCreatedAt(Instant.now());
        category.setUpdatedAt(Instant.now());
        category.setCreatedBy("admin");
        category.setUpdatedBy("admin");
        return category;
    }

    public static CategoryDTO createCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getActive()
        );
    }

    public static CategoryDetailsDTO createCategoryDetailsDTO(Category category) {
        CategoryDetailsDTO dto = new CategoryDetailsDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setActive(category.getActive());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        dto.setCreatedBy(category.getCreatedBy());
        dto.setUpdatedBy(category.getUpdatedBy());
        return dto;
    }

    public static CategoryInsertDTO createCategoryInsertDTO() {
        CategoryInsertDTO dto = new CategoryInsertDTO();
        dto.setName("Consoles");
        return dto;
    }

    public static CategoryUpdateDTO createCategoryUpdateDTO() {
        CategoryUpdateDTO dto = new CategoryUpdateDTO();
        dto.setName("Jogos");
        return dto;
    }
}
