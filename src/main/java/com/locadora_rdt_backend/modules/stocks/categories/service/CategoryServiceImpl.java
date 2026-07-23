package com.locadora_rdt_backend.modules.stocks.categories.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.stocks.categories.constants.CategoryErrorMessages;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryInsertDTO;
import com.locadora_rdt_backend.modules.stocks.categories.dto.CategoryUpdateDTO;
import com.locadora_rdt_backend.modules.stocks.categories.mapper.CategoryMapper;
import com.locadora_rdt_backend.modules.stocks.categories.model.Category;
import com.locadora_rdt_backend.modules.stocks.categories.repository.CategoryRepository;
import com.locadora_rdt_backend.shared.service.ImageUploadSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public CategoryServiceImpl(
            CategoryRepository repository,
            CategoryMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(normalizeName(name), pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDetailsDTO findById(Long id) {
        Category entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CategoryErrorMessages.CATEGORY_NOT_FOUND
                ));

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Category findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CategoryErrorMessages.CATEGORY_NOT_FOUND
                ));
    }

    @Override
    @Transactional
    public CategoryDTO insert(CategoryInsertDTO dto) {
        Category entity = mapper.toEntity(dto);

        entity.setActive(true);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public CategoryDTO update(Long id, CategoryUpdateDTO dto) {
        Category entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CategoryErrorMessages.CATEGORY_NOT_FOUND
                ));

        mapper.copyToEntity(dto, entity);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void updateImage(Long id, MultipartFile file) {
        Category entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CategoryErrorMessages.CATEGORY_NOT_FOUND
                ));

        ImageUploadSupport.validatePhoto(file);
        entity.setImage(ImageUploadSupport.readBytes(file, "Falha ao ler bytes do arquivo."));
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        CategoryErrorMessages.CATEGORY_NOT_FOUND
                ));

        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    CategoryErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    @Override
    @Transactional
    public void deleteAll(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Lista de ids vazia");
        }

        List<Long> existingIds = repository.findAllById(ids)
                .stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        if (existingIds.size() != ids.size()) {
            throw new ResourceNotFoundException("Um ou mais IDs não existem");
        }

        try {
            repository.deleteAllByIds(ids);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    CategoryErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    @Override
    @Transactional
    public void changeActiveStatus(Long id, boolean active) {
        try {
            int updated = repository.updateActiveById(id, active);

            if (updated == 0) {
                throw new ResourceNotFoundException(
                        CategoryErrorMessages.CATEGORY_NOT_FOUND
                );
            }
        } catch (DataAccessException e) {
            throw new DatabaseException("Error changing category status.");
        }
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
