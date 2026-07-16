package com.locadora_rdt_backend.tests.modules.suppliers.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.FileException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierDetailsDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierInsertDTO;
import com.locadora_rdt_backend.modules.suppliers.dto.SupplierUpdateDTO;
import com.locadora_rdt_backend.modules.suppliers.mapper.SupplierMapper;
import com.locadora_rdt_backend.modules.suppliers.model.Address;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.suppliers.repository.SupplierRepository;
import com.locadora_rdt_backend.modules.suppliers.service.SupplierServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5778")
class SupplierServiceTests {

    @InjectMocks
    private SupplierServiceImpl service;

    @Mock
    private SupplierRepository repository;

    @Mock
    private SupplierMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;
    private Supplier supplier;
    private SupplierDTO supplierDTO;
    private SupplierDetailsDTO detailsDTO;
    private SupplierInsertDTO insertDTO;
    private SupplierUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;
        supplier = new Supplier();
        supplier.setId(existingId);
        supplier.setName("Fornecedor");
        supplier.setCnpj("123");
        supplier.setEmail("fornecedor@email.com");
        supplier.setPhoneNumber("11999999999");
        supplierDTO = new SupplierDTO();
        supplierDTO.setId(existingId);
        supplierDTO.setName("Fornecedor");
        detailsDTO = new SupplierDetailsDTO();
        detailsDTO.setId(existingId);
        detailsDTO.setName("Fornecedor");
        insertDTO = createInsertDTO();
        updateDTO = new SupplierUpdateDTO();
        fillSupplierDTO(updateDTO);
    }

    @Test
    void findAllPagedShouldReturnPageAndNormalizeName() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Supplier> page = new PageImpl<>(List.of(supplier));

        Mockito.when(repository.findByNameContainingIgnoreCase("Fornecedor", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(supplier)).thenReturn(supplierDTO);

        Page<SupplierDTO> result = service.findAllPaged(" Fornecedor ", pageRequest);

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldNormalizeNullName() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findByNameContainingIgnoreCase("", pageRequest)).thenReturn(new PageImpl<>(List.of()));

        service.findAllPaged(null, pageRequest);

        Mockito.verify(repository).findByNameContainingIgnoreCase("", pageRequest);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.when(mapper.toDetailsDTO(supplier)).thenReturn(detailsDTO);

        SupplierDetailsDTO result = service.findById(existingId);

        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    void insertShouldReturnDTOWhenUniqueFieldsAreValid() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(supplier);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(supplier)).thenReturn(supplier);
        Mockito.when(mapper.toDTO(supplier)).thenReturn(supplierDTO);

        SupplierDTO result = service.insert(insertDTO);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("admin", supplier.getCreatedBy());
    }

    @Test
    void insertShouldThrowWhenUniqueFieldsExist() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(supplier);
        Mockito.when(repository.existsByCnpjAndIdNot(supplier.getCnpj(), -1L)).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(insertDTO));
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(supplier)).thenReturn(supplier);
        Mockito.when(mapper.toDTO(supplier)).thenReturn(supplierDTO);

        SupplierDTO result = service.update(existingId, updateDTO);

        Assertions.assertEquals(existingId, result.getId());
        Mockito.verify(mapper).copyToEntity(updateDTO, supplier);
    }

    @Test
    void updateShouldThrowWhenEmailExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.when(repository.existsByEmailIgnoreCaseAndIdNot(supplier.getEmail(), existingId)).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(existingId, updateDTO));
    }

    @Test
    void updateImageShouldSaveValidImage() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");

        service.updateImage(existingId, file);

        Assertions.assertArrayEquals(new byte[]{1}, supplier.getImage());
        Assertions.assertEquals("image/png", supplier.getImageContentType());
        Mockito.verify(repository).save(supplier);
    }

    @Test
    void updateImageShouldThrowWhenFileIsInvalid() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(supplier));

        Assertions.assertThrows(FileException.class, () -> service.updateImage(existingId, null));
        Assertions.assertThrows(FileException.class, () ->
                service.updateImage(existingId, new MockMultipartFile("file", "image.gif", "image/gif", new byte[]{1})));
    }

    @Test
    void updateImageShouldThrowWhenReadFails() throws IOException {
        MockMultipartFile file = Mockito.mock(MockMultipartFile.class);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getContentType()).thenReturn("image/png");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getBytes()).thenThrow(IOException.class);

        Assertions.assertThrows(FileException.class, () -> service.updateImage(existingId, file));
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(supplier));

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenIntegrityViolation() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(existingId));
    }

    private SupplierInsertDTO createInsertDTO() {
        SupplierInsertDTO dto = new SupplierInsertDTO();
        fillSupplierDTO(dto);
        return dto;
    }

    private void fillSupplierDTO(SupplierInsertDTO dto) {
        dto.setName("Fornecedor");
        dto.setTradeName("Fantasia");
        dto.setCompanyName("Empresa");
        dto.setCnpj("123");
        dto.setAddress(createAddress());
        dto.setEmail("fornecedor@email.com");
        dto.setPhoneNumber("11999999999");
    }

    private Address createAddress() {
        Address address = new Address();
        address.setZipCode("30100-000");
        address.setStreet("Rua A");
        address.setNumber("100");
        address.setNeighborhood("Centro");
        address.setCity("Belo Horizonte");
        address.setState("MG");
        return address;
    }
}
