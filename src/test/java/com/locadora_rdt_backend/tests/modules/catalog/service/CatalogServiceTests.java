package com.locadora_rdt_backend.tests.modules.catalog.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.rentals.catalog.service.CatalogServiceImpl;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.items.mapper.ItemMapper;
import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import com.locadora_rdt_backend.modules.stocks.items.repository.ItemRepository;
import com.locadora_rdt_backend.tests.modules.inventory.items.factory.ItemFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5778")
class CatalogServiceTests {

    @InjectMocks
    private CatalogServiceImpl service;

    @Mock
    private ItemRepository repository;

    @Mock
    private ItemMapper mapper;

    private Long existingId;
    private Long nonExistingId;
    private Long categoryId;

    private Item item;
    private ItemDTO itemDTO;
    private ItemDetailsDTO itemDetailsDTO;

    private PageImpl<Item> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;
        categoryId = 1L;

        item = ItemFactory.createItem();
        itemDTO = ItemFactory.createItemDTO(item);
        itemDetailsDTO = ItemFactory.createItemDetailsDTO(item);

        page = new PageImpl<>(List.of(item));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("", -1L, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        Page<ItemDTO> result = service.findAllPaged("", null, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoData() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Item> emptyPage = new PageImpl<>(List.of());

        Mockito.when(repository.findForCatalog("", -1L, pageRequest)).thenReturn(emptyPage);

        Page<ItemDTO> result = service.findAllPaged("", null, pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllPagedShouldCallRepository() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("", -1L, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged("", null, pageRequest);

        Mockito.verify(repository).findForCatalog("", -1L, pageRequest);
    }

    @Test
    void findAllPagedShouldTrimName() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("Playstation 5", -1L, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged(" Playstation 5 ", null, pageRequest);

        Mockito.verify(repository).findForCatalog("Playstation 5", -1L, pageRequest);
    }

    @Test
    void findAllPagedShouldUseEmptyNameWhenNameIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("", -1L, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged(null, null, pageRequest);

        Mockito.verify(repository).findForCatalog("", -1L, pageRequest);
    }

    @Test
    void findAllPagedShouldUseCategoryIdWhenCategoryIdIsValid() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("", categoryId, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged("", categoryId, pageRequest);

        Mockito.verify(repository).findForCatalog("", categoryId, pageRequest);
    }

    @Test
    void findAllPagedShouldUseDisabledCategoryWhenCategoryIdIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("", -1L, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged("", null, pageRequest);

        Mockito.verify(repository).findForCatalog("", -1L, pageRequest);
    }

    @Test
    void findAllPagedShouldUseDisabledCategoryWhenCategoryIdIsZero() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("", -1L, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged("", 0L, pageRequest);

        Mockito.verify(repository).findForCatalog("", -1L, pageRequest);
    }

    @Test
    void findAllPagedShouldUseDisabledCategoryWhenCategoryIdIsNegative() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("", -1L, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged("", -5L, pageRequest);

        Mockito.verify(repository).findForCatalog("", -1L, pageRequest);
    }

    @Test
    void findAllPagedShouldMapItemsToDTO() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findForCatalog("", -1L, pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged("", null, pageRequest);

        Mockito.verify(mapper).toDTO(item);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(item));

        Mockito.when(mapper.toDetailsDTO(item))
                .thenReturn(itemDetailsDTO);

        ItemDetailsDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    void findByIdShouldCallRepository() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(item));

        Mockito.when(mapper.toDetailsDTO(item))
                .thenReturn(itemDetailsDTO);

        service.findById(existingId);

        Mockito.verify(repository).findById(existingId);
    }

    @Test
    void findByIdShouldMapEntityToDetailsDTO() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(item));

        Mockito.when(mapper.toDetailsDTO(item))
                .thenReturn(itemDetailsDTO);

        service.findById(existingId);

        Mockito.verify(mapper).toDetailsDTO(item);
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(item));

        Item result = service.findEntityById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(item, result);
    }

    @Test
    void findEntityByIdShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findEntityById(nonExistingId);
        });
    }
}
