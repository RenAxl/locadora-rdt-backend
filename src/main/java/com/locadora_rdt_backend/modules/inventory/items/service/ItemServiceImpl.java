package com.locadora_rdt_backend.modules.inventory.items.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.rental.categories.model.Category;
import com.locadora_rdt_backend.modules.rental.categories.service.CategoryService;
import com.locadora_rdt_backend.modules.inventory.items.constants.ItemErrorMessages;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemInsertDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemUpdateDTO;
import com.locadora_rdt_backend.modules.inventory.items.mapper.ItemMapper;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.inventory.items.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final CategoryService categoryService;
    private final ItemMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public ItemServiceImpl(
            ItemRepository repository,
            CategoryService categoryService,
            ItemMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.categoryService = categoryService;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(normalizeName(name), pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDetailsDTO findById(Long id) {
        Item entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ItemErrorMessages.ITEM_NOT_FOUND
                ));

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Item findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ItemErrorMessages.ITEM_NOT_FOUND
                ));
    }

    @Override
    @Transactional
    public ItemDTO insert(ItemInsertDTO dto) {
        Item entity = mapper.toEntity(dto);

        Category category = categoryService.findEntityById(dto.getCategoryId());

        entity.setCategory(category);
        entity.setActive(true);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public ItemDTO update(Long id, ItemUpdateDTO dto) {
        Item entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ItemErrorMessages.ITEM_NOT_FOUND
                ));

        Category category = categoryService.findEntityById(dto.getCategoryId());

        mapper.copyToEntity(dto, entity);
        entity.setCategory(category);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void updateImage(Long id, MultipartFile file) {
        Item entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ItemErrorMessages.ITEM_NOT_FOUND
                ));

        ImageUploadSupport.validatePhoto(file);
        entity.setImage(ImageUploadSupport.readBytes(file, "Falha ao ler bytes do arquivo."));
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());

        repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Item entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ItemErrorMessages.ITEM_NOT_FOUND
                ));

        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    ItemErrorMessages.DATABASE_INTEGRITY_VIOLATION
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
                .map(Item::getId)
                .collect(Collectors.toList());

        if (existingIds.size() != ids.size()) {
            throw new ResourceNotFoundException("Um ou mais IDs não existem");
        }

        try {
            repository.deleteAllByIds(ids);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    ItemErrorMessages.DATABASE_INTEGRITY_VIOLATION
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
                        ItemErrorMessages.ITEM_NOT_FOUND
                );
            }
        } catch (DataAccessException e) {
            throw new DatabaseException("Error changing item status.");
        }
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
