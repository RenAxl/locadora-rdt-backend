package com.locadora_rdt_backend.tests.modules.inventory.items.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemInsertDTO;
import com.locadora_rdt_backend.modules.inventory.items.dto.ItemUpdateDTO;
import com.locadora_rdt_backend.modules.inventory.items.mapper.ItemMapper;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.inventory.items.repository.ItemRepository;
import com.locadora_rdt_backend.modules.inventory.items.service.ItemServiceImpl;
import com.locadora_rdt_backend.modules.categories.model.Category;
import com.locadora_rdt_backend.modules.categories.service.CategoryService;
import com.locadora_rdt_backend.tests.modules.inventory.items.factory.ItemFactory;
import com.locadora_rdt_backend.tests.modules.rental.categories.factory.CategoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5778")
class ItemServiceTests {

    @InjectMocks
    private ItemServiceImpl service;

    @Mock
    private ItemRepository repository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ItemMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;

    private Item item;
    private ItemDTO itemDTO;
    private ItemDetailsDTO itemDetailsDTO;
    private ItemInsertDTO insertDTO;
    private ItemUpdateDTO updateDTO;
    private Category category;

    private PageImpl<Item> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        item = ItemFactory.createItem();
        itemDTO = ItemFactory.createItemDTO(item);
        itemDetailsDTO = ItemFactory.createItemDetailsDTO(item);
        insertDTO = ItemFactory.createItemInsertDTO();
        updateDTO = ItemFactory.createItemUpdateDTO();
        category = CategoryFactory.createCategory();

        page = new PageImpl<>(List.of(item));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        Page<ItemDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoData() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Item> emptyPage = new PageImpl<>(List.of());

        Mockito.when(repository.find("", pageRequest)).thenReturn(emptyPage);

        Page<ItemDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllPagedShouldCallRepository() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged("", pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findAllPagedShouldTrimName() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("Playstation 5", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged(" Playstation 5 ", pageRequest);

        Mockito.verify(repository).find("Playstation 5", pageRequest);
    }

    @Test
    void findAllPagedShouldUseEmptyNameWhenNameIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.findAllPaged(null, pageRequest);

        Mockito.verify(repository).find("", pageRequest);
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

    @Test
    void insertShouldReturnDTO() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(item);
        Mockito.when(categoryService.findEntityById(insertDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        ItemDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(item.getName(), result.getName());
    }

    @Test
    void insertShouldCallRepositorySave() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(item);
        Mockito.when(categoryService.findEntityById(insertDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.insert(insertDTO);

        Mockito.verify(repository).save(item);
    }

    @Test
    void insertShouldMapEntityCorrectly() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(item);
        Mockito.when(categoryService.findEntityById(insertDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.insert(insertDTO);

        Mockito.verify(mapper).toEntity(insertDTO);
    }

    @Test
    void insertShouldSetCategory() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(item);
        Mockito.when(categoryService.findEntityById(insertDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.insert(insertDTO);

        Assertions.assertEquals(category, item.getCategory());
    }

    @Test
    void insertShouldSetCreatedBy() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(item);
        Mockito.when(categoryService.findEntityById(insertDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.insert(insertDTO);

        Assertions.assertEquals("admin", item.getCreatedBy());
    }

    @Test
    void insertShouldSetActiveTrue() {
        item.setActive(false);

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(item);
        Mockito.when(categoryService.findEntityById(insertDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.insert(insertDTO);

        Assertions.assertTrue(item.getActive());
    }

    @Test
    void insertShouldThrowWhenCategoryDoesNotExist() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(item);
        Mockito.when(categoryService.findEntityById(insertDTO.getCategoryId()))
                .thenThrow(ResourceNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.insert(insertDTO);
        });
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.when(categoryService.findEntityById(updateDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        ItemDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    void updateShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, updateDTO);
        });
    }

    @Test
    void updateShouldThrowWhenCategoryDoesNotExist() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.when(categoryService.findEntityById(updateDTO.getCategoryId()))
                .thenThrow(ResourceNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(existingId, updateDTO);
        });
    }

    @Test
    void updateShouldCallMapperUpdateEntity() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.when(categoryService.findEntityById(updateDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.update(existingId, updateDTO);

        Mockito.verify(mapper).copyToEntity(updateDTO, item);
    }

    @Test
    void updateShouldSetCategory() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.when(categoryService.findEntityById(updateDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.update(existingId, updateDTO);

        Assertions.assertEquals(category, item.getCategory());
    }

    @Test
    void updateShouldSetUpdatedBy() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.when(categoryService.findEntityById(updateDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.update(existingId, updateDTO);

        Assertions.assertEquals("admin", item.getUpdatedBy());
    }

    @Test
    void updateShouldNotChangeActiveStatus() {
        item.setActive(false);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.when(categoryService.findEntityById(updateDTO.getCategoryId())).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(item)).thenReturn(item);
        Mockito.when(mapper.toDTO(item)).thenReturn(itemDTO);

        service.update(existingId, updateDTO);

        Assertions.assertFalse(item.getActive());
    }

    @Test
    void updateImageShouldSaveValidImage() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");

        service.updateImage(existingId, file);

        Assertions.assertArrayEquals(new byte[]{1}, item.getImage());
        Assertions.assertEquals("admin", item.getUpdatedBy());
        Mockito.verify(repository).save(item);
    }

    @Test
    void updateImageShouldThrowWhenItemDoesNotExist() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.updateImage(nonExistingId, file);
        });
    }

    @Test
    void updateImageShouldThrowWhenFileIsNullOrEmpty() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId, null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId, new MockMultipartFile("file", new byte[]{}));
        });
    }

    @Test
    void updateImageShouldThrowWhenFileTypeIsInvalid() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId,
                    new MockMultipartFile("file", "image.gif", "image/gif", new byte[]{1}));
        });
    }

    @Test
    void updateImageShouldThrowWhenFileContentTypeIsNull() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId,
                    new MockMultipartFile("file", "image", null, new byte[]{1}));
        });
    }

    @Test
    void updateImageShouldThrowWhenFileIsTooLarge() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId,
                    new MockMultipartFile("file", "image.png", "image/png", new byte[2 * 1024 * 1024 + 1]));
        });
    }

    @Test
    void updateImageShouldThrowWhenFileReadFails() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getContentType()).thenReturn("image/png");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getBytes()).thenThrow(IOException.class);

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.updateImage(existingId, file);
        });
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
    }

    @Test
    void deleteShouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(item));
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(existingId);
        });
    }

    @Test
    void deleteAllShouldDeleteExistingIds() {
        List<Long> ids = List.of(existingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(item));

        service.deleteAll(ids);

        Mockito.verify(repository).deleteAllByIds(ids);
    }

    @Test
    void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsAreNullOrEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.deleteAll(null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.deleteAll(List.of());
        });
    }

    @Test
    void deleteAllShouldThrowResourceNotFoundWhenAnyIdDoesNotExist() {
        List<Long> ids = List.of(existingId, nonExistingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(item));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteAll(ids);
        });
    }

    @Test
    void deleteAllShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        List<Long> ids = List.of(existingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(item));
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.deleteAll(ids);
        });
    }

    @Test
    void changeActiveStatusShouldUpdateWhenIdExists() {
        Mockito.when(repository.updateActiveById(existingId, false)).thenReturn(1);

        service.changeActiveStatus(existingId, false);

        Mockito.verify(repository).updateActiveById(existingId, false);
    }

    @Test
    void changeActiveStatusShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.updateActiveById(nonExistingId, false)).thenReturn(0);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.changeActiveStatus(nonExistingId, false);
        });
    }

    @Test
    void changeActiveStatusShouldThrowWhenDataAccessFails() {
        Mockito.when(repository.updateActiveById(existingId, false))
                .thenThrow(new DataAccessResourceFailureException("fail"));

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.changeActiveStatus(existingId, false);
        });
    }
}
