package com.locadora_rdt_backend.tests.modules.rental.categories.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.categories.dto.CategoryDTO;
import com.locadora_rdt_backend.modules.categories.dto.CategoryDetailsDTO;
import com.locadora_rdt_backend.modules.categories.dto.CategoryInsertDTO;
import com.locadora_rdt_backend.modules.categories.dto.CategoryUpdateDTO;
import com.locadora_rdt_backend.modules.categories.mapper.CategoryMapper;
import com.locadora_rdt_backend.modules.categories.model.Category;
import com.locadora_rdt_backend.modules.categories.repository.CategoryRepository;
import com.locadora_rdt_backend.modules.categories.service.CategoryServiceImpl;
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
class CategoryServiceTests {

    @InjectMocks
    private CategoryServiceImpl service;

    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;

    private Category category;
    private CategoryDTO categoryDTO;
    private CategoryDetailsDTO categoryDetailsDTO;
    private CategoryInsertDTO insertDTO;
    private CategoryUpdateDTO updateDTO;

    private PageImpl<Category> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        category = CategoryFactory.createCategory();
        categoryDTO = CategoryFactory.createCategoryDTO(category);
        categoryDetailsDTO = CategoryFactory.createCategoryDetailsDTO(category);
        insertDTO = CategoryFactory.createCategoryInsertDTO();
        updateDTO = CategoryFactory.createCategoryUpdateDTO();

        page = new PageImpl<>(List.of(category));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        Page<CategoryDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenNoData() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Category> emptyPage = new PageImpl<>(List.of());

        Mockito.when(repository.find("", pageRequest)).thenReturn(emptyPage);

        Page<CategoryDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllPagedShouldCallRepository() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.findAllPaged("", pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findAllPagedShouldTrimName() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("Consoles", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.findAllPaged(" Consoles ", pageRequest);

        Mockito.verify(repository).find("Consoles", pageRequest);
    }

    @Test
    void findAllPagedShouldUseEmptyNameWhenNameIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.findAllPaged(null, pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(category));

        Mockito.when(mapper.toDetailsDTO(category))
                .thenReturn(categoryDetailsDTO);

        CategoryDetailsDTO result = service.findById(existingId);

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
                .thenReturn(Optional.of(category));

        Mockito.when(mapper.toDetailsDTO(category))
                .thenReturn(categoryDetailsDTO);

        service.findById(existingId);

        Mockito.verify(repository).findById(existingId);
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {
        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(category));

        Category result = service.findEntityById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(category, result);
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
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(category.getName(), result.getName());
    }

    @Test
    void insertShouldCallRepositorySave() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.insert(insertDTO);

        Mockito.verify(repository).save(category);
    }

    @Test
    void insertShouldMapEntityCorrectly() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.insert(insertDTO);

        Mockito.verify(mapper).toEntity(insertDTO);
    }

    @Test
    void insertShouldSetCreatedBy() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.insert(insertDTO);

        Assertions.assertEquals("admin", category.getCreatedBy());
    }

    @Test
    void insertShouldSetActiveTrue() {
        category.setActive(false);

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(category);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.insert(insertDTO);

        Assertions.assertTrue(category.getActive());
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = service.update(existingId, updateDTO);

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
    void updateShouldCallMapperUpdateEntity() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.update(existingId, updateDTO);

        Mockito.verify(mapper).copyToEntity(updateDTO, category);
    }

    @Test
    void updateShouldSetUpdatedBy() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.update(existingId, updateDTO);

        Assertions.assertEquals("admin", category.getUpdatedBy());
    }

    @Test
    void updateShouldNotChangeActiveStatus() {
        category.setActive(false);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(category)).thenReturn(category);
        Mockito.when(mapper.toDTO(category)).thenReturn(categoryDTO);

        service.update(existingId, updateDTO);

        Assertions.assertFalse(category.getActive());
    }

    @Test
    void updateImageShouldSaveValidImage() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");

        service.updateImage(existingId, file);

        Assertions.assertArrayEquals(new byte[]{1}, category.getImage());
        Assertions.assertEquals("admin", category.getUpdatedBy());
        Mockito.verify(repository).save(category);
    }

    @Test
    void updateImageShouldThrowWhenCategoryDoesNotExist() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.updateImage(nonExistingId, file);
        });
    }

    @Test
    void updateImageShouldThrowWhenFileIsNullOrEmpty() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId, null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId, new MockMultipartFile("file", new byte[]{}));
        });
    }

    @Test
    void updateImageShouldThrowWhenFileTypeIsInvalid() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId,
                    new MockMultipartFile("file", "image.gif", "image/gif", new byte[]{1}));
        });
    }

    @Test
    void updateImageShouldThrowWhenFileContentTypeIsNull() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId,
                    new MockMultipartFile("file", "image", null, new byte[]{1}));
        });
    }

    @Test
    void updateImageShouldThrowWhenFileIsTooLarge() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updateImage(existingId,
                    new MockMultipartFile("file", "image.png", "image/png", new byte[2 * 1024 * 1024 + 1]));
        });
    }

    @Test
    void updateImageShouldThrowWhenFileReadFails() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
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
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));

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
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.doThrow(DataIntegrityViolationException.class)
                .when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(existingId);
        });
    }

    @Test
    void deleteAllShouldDeleteExistingIds() {
        List<Long> ids = List.of(existingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(category));

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

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(category));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteAll(ids);
        });
    }

    @Test
    void deleteAllShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        List<Long> ids = List.of(existingId);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(category));
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
