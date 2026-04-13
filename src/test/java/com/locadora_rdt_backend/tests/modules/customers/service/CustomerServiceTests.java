package com.locadora_rdt_backend.tests.modules.customers.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class CustomerServiceTests {

    @InjectMocks
    private CustomerService service;

    @Mock
    private CustomerRepository repository;

    private String existingName;
    private String nonExistingName;
    private Customer customer;
    private PageImpl<Customer> page;
    private PageImpl<Customer> emptyPage;
    private CustomerInsertDTO insertDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingName = "Maria";
        nonExistingName = "João";

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Maria Silva");
        customer.setCpf("12345678900");
        customer.setEmail("maria@email.com");
        customer.setPhone("31999999999");
        customer.setAddress("Rua A, 100");
        customer.setActive(true);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());

        insertDTO = new CustomerInsertDTO();
        insertDTO.setName("Maria Silva");
        insertDTO.setCpf("12345678900");
        insertDTO.setEmail("maria@email.com");
        insertDTO.setPhone("31999999999");
        insertDTO.setAddress("Rua A, 100");

        page = new PageImpl<>(List.of(customer));
        emptyPage = new PageImpl<>(List.of());

        Mockito.when(repository.find(ArgumentMatchers.eq(existingName), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(page);

        Mockito.when(repository.find(ArgumentMatchers.eq(nonExistingName), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(emptyPage);

        Mockito.when(repository.find(ArgumentMatchers.eq(""), ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer entity = invocation.getArgument(0);
                    entity.setId(1L);
                    return entity;
                });
    }

    @Test
    public void findAllPagedShouldReturnPageWhenNameExists() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<CustomerDTO> result = service.findAllPaged(existingName, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(repository, Mockito.times(1)).find(existingName, pageRequest);
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenNameDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<CustomerDTO> result = service.findAllPaged(nonExistingName, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());
        Mockito.verify(repository, Mockito.times(1)).find(nonExistingName, pageRequest);
    }

    @Test
    public void findAllPagedShouldMapCustomerToCustomerDTO() {
        String name = "";
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<CustomerDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(customer.getId(), result.getContent().get(0).getId());
        Assertions.assertEquals(customer.getName(), result.getContent().get(0).getName());
        Assertions.assertEquals(customer.getCpf(), result.getContent().get(0).getCpf());
        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
    }

    @Test
    public void insertShouldSaveCustomerAndReturnDTO() {
        CustomerDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(insertDTO.getName(), result.getName());
        Assertions.assertEquals(insertDTO.getCpf(), result.getCpf());
        Assertions.assertEquals(insertDTO.getEmail(), result.getEmail());
        Assertions.assertEquals(insertDTO.getPhone(), result.getPhone());
        Assertions.assertEquals(insertDTO.getAddress(), result.getAddress());

        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void insertShouldSetActiveFalseAndCreatedAt() {
        service.insert(insertDTO);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(repository).save(customerCaptor.capture());

        Customer savedEntity = customerCaptor.getValue();

        Assertions.assertNotNull(savedEntity);
        Assertions.assertFalse(savedEntity.getActive());
        Assertions.assertNotNull(savedEntity.getCreatedAt());
    }

    @Test
    public void insertShouldCopyInsertDtoDataToEntity() {
        service.insert(insertDTO);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(repository).save(customerCaptor.capture());

        Customer savedEntity = customerCaptor.getValue();

        Assertions.assertEquals(insertDTO.getName(), savedEntity.getName());
        Assertions.assertEquals(insertDTO.getCpf(), savedEntity.getCpf());
        Assertions.assertEquals(insertDTO.getEmail(), savedEntity.getEmail());
        Assertions.assertEquals(insertDTO.getPhone(), savedEntity.getPhone());
        Assertions.assertEquals(insertDTO.getAddress(), savedEntity.getAddress());
    }

    @Test
    public void updatePhotoShouldUpdatePhotoWhenValidFile() throws Exception {
        Long existingId = 1L;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(customer));

        service.updatePhoto(existingId, file);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(repository).save(captor.capture());

        Customer saved = captor.getValue();

        Assertions.assertNotNull(saved.getPhoto());
        Assertions.assertEquals("image/jpeg", saved.getPhotoContentType());
    }

    @Test
    public void updatePhotoShouldThrowExceptionWhenFileIsEmpty() {
        Long existingId = 1L;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                new byte[]{} // vazio
        );

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(customer));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updatePhoto(existingId, file);
        });

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void findEntityByIdShouldReturnCustomerWhenIdExists() {
        Long existingId = 1L;

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(customer));

        Customer result = service.findEntityById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Mockito.verify(repository).findById(existingId);
    }

    @Test
    public void updateShouldUpdateCustomerWhenIdExists() {
        Long existingId = 1L;

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
        updateDTO.setName("Maria Atualizada");
        updateDTO.setCpf("12345678900");
        updateDTO.setEmail("nova@email.com");
        updateDTO.setPhone("31888888888");
        updateDTO.setAddress("Rua B, 200");

        Mockito.when(repository.getOne(existingId)).thenReturn(customer);

        CustomerDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(updateDTO.getName(), result.getName());
        Assertions.assertEquals(updateDTO.getEmail(), result.getEmail());

        Mockito.verify(repository).save(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void updateShouldSetUpdatedAtWhenUpdatingCustomer() {
        Long existingId = 1L;

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
        updateDTO.setName("Maria Atualizada");

        Mockito.when(repository.getOne(existingId)).thenReturn(customer);

        service.update(existingId, updateDTO);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(repository).save(captor.capture());

        Customer savedEntity = captor.getValue();

        Assertions.assertNotNull(savedEntity.getUpdatedAt());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Long nonExistingId = 999L;

        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();

        Mockito.when(repository.getOne(nonExistingId))
                .thenThrow(javax.persistence.EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, updateDTO);
        });

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Long existingId = 1L;

        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldCallRepositoryDeleteById() {
        Long existingId = 1L;

        Mockito.doNothing().when(repository).deleteById(existingId);

        service.delete(existingId);

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Long nonExistingId = 999L;

        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteAllShouldDeleteWhenIdsExist() {
        List<Long> ids = List.of(1L, 2L);

        Customer c1 = new Customer();
        c1.setId(1L);

        Customer c2 = new Customer();
        c2.setId(2L);

        Mockito.when(repository.findAllById(ids))
                .thenReturn(List.of(c1, c2));

        Mockito.doNothing().when(repository).deleteAllByIds(ids);

        Assertions.assertDoesNotThrow(() -> {
            service.deleteAll(ids);
        });

        Mockito.verify(repository, Mockito.times(1)).findAllById(ids);
        Mockito.verify(repository, Mockito.times(1)).deleteAllByIds(ids);
    }

    @Test
    public void deleteAllShouldThrowIllegalArgumentExceptionWhenListIsEmpty() {
        List<Long> ids = List.of();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.deleteAll(ids);
        });

        Mockito.verify(repository, Mockito.never()).deleteAllByIds(Mockito.any());
    }

    @Test
    public void deleteAllShouldThrowResourceNotFoundExceptionWhenIdsDoNotExist() {
        List<Long> ids = List.of(1L, 2L);

        Customer c1 = new Customer();
        c1.setId(1L);

        // Simula que só 1 ID existe
        Mockito.when(repository.findAllById(ids))
                .thenReturn(List.of(c1));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteAll(ids);
        });

        Mockito.verify(repository, Mockito.times(1)).findAllById(ids);
        Mockito.verify(repository, Mockito.never()).deleteAllByIds(Mockito.any());
    }

    @Test
    public void changeActiveStatusShouldUpdateWhenIdExists() {
        Long existingId = 1L;
        boolean active = true;

        Mockito.when(repository.updateActiveById(existingId, active))
                .thenReturn(1);

        Assertions.assertDoesNotThrow(() -> {
            service.changeActiveStatus(existingId, active);
        });

        Mockito.verify(repository, Mockito.times(1))
                .updateActiveById(existingId, active);
    }

    @Test
    public void changeActiveStatusShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Long nonExistingId = 999L;
        boolean active = true;

        Mockito.when(repository.updateActiveById(nonExistingId, active))
                .thenReturn(0);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.changeActiveStatus(nonExistingId, active);
        });

        Mockito.verify(repository, Mockito.times(1))
                .updateActiveById(nonExistingId, active);
    }

    @Test
    public void changeActiveStatusShouldThrowRuntimeExceptionWhenDatabaseErrorOccurs() {
        Long existingId = 1L;
        boolean active = true;

        Mockito.when(repository.updateActiveById(existingId, active))
                .thenThrow(new org.springframework.dao.DataAccessException("DB error") {});

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.changeActiveStatus(existingId, active);
        });

        Mockito.verify(repository, Mockito.times(1))
                .updateActiveById(existingId, active);
    }
}