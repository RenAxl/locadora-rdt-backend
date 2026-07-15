package com.locadora_rdt_backend.modules.categories.mapper;

import com.locadora_rdt_backend.modules.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.categories.dto.CategoryDetailsDTO;
import com.locadora_rdt_backend.modules.categories.dto.CategoryInsertDTO;
import com.locadora_rdt_backend.modules.categories.dto.CategoryUpdateDTO;
import com.locadora_rdt_backend.modules.categories.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category entity) {
        if (entity == null) {
            return null;
        }

        return new CategoryDTO(
                entity.getId(),
                entity.getName(),
                entity.getActive()
        );
    }

    public CategoryDetailsDTO toDetailsDTO(Category entity) {
        if (entity == null) {
            return null;
        }

        CategoryDetailsDTO dto = new CategoryDetailsDTO();
        dto.setId(entity.getId());
        dto.setVersion(entity.getVersion());
        dto.setName(entity.getName());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        return dto;
    }

    public Category toEntity(CategoryInsertDTO dto) {
        if (dto == null) {
            return null;
        }

        Category entity = new Category();
        entity.setName(dto.getName());
        entity.setActive(true);

        return entity;
    }

    public void copyToEntity(CategoryUpdateDTO dto, Category entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
    }
}
