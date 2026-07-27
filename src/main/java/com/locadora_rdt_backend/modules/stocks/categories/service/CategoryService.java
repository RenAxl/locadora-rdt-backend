package com.locadora_rdt_backend.modules.stocks.categories.service;

import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryInsertDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryUpdateDTO;
import com.locadora_rdt_backend.modules.stocks.categories.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {

    Page<CategoryDTO> findAllPaged(
            String name,
            PageRequest pageRequest
    );

    CategoryDetailsDTO findById(Long id);

    Category findEntityById(Long id);

    CategoryDTO insert(CategoryInsertDTO dto);

    CategoryDTO update(
            Long id,
            CategoryUpdateDTO dto
    );

    void updateImage(
            Long id,
            MultipartFile file
    );

    void delete(Long id);

    void deleteAll(List<Long> ids);

    void changeActiveStatus(
            Long id,
            boolean active
    );
}
