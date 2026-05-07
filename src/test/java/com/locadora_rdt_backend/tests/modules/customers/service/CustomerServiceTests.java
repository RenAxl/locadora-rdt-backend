package com.locadora_rdt_backend.tests.modules.customers.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDetailsDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.mapper.CustomerMapper;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.service.CustomerService;
import com.locadora_rdt_backend.tests.modules.customers.factory.CustomerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTests {

    @InjectMocks
    private CustomerService service;

    @Mock
    private CustomerRepository repository;

    @Spy
    private CustomerMapper mapper = new CustomerMapper();

    private Long existingId;
    private Long nonExistingId;

    private Customer customer;
    private CustomerInsertDTO insertDTO;
    private CustomerUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        customer = CustomerFactory.createCustomer();
        insertDTO = CustomerFactory.createCustomerInsertDTO();
        updateDTO = CustomerFactory.createCustomerUpdateDTO();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void findAllPagedShouldReturnPageOfCustomerDTOWhenNameExists() {
        String name = "Maria";
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Customer> page = new PageImpl<>(List.of(customer));

        Mockito.when(repository.find(name, pageRequest)).thenReturn(page);

        Page<CustomerDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(customer.getId(), result.getContent().get(0).getId());
        Assertions.assertEquals(customer.getName(), result.getContent().get(0).getName());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
        Mockito.verify(mapper, Mockito.times(1)).toDTO(customer);
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenNameDoesNotExist() {
        String name = "João";
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Customer> emptyPage = new PageImpl<>(Collections.emptyList());

        Mockito.when(repository.find(name, pageRequest)).thenReturn(emptyPage);

        Page<CustomerDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
        Mockito.verify(mapper, Mockito.never()).toDTO(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void findAllPagedShouldCallRepositoryWithEmptyName() {
        String name = "";
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Customer> page = new PageImpl<>(List.of(customer));

        Mockito.when(repository.find(name, pageRequest)).thenReturn(page);

        Page<CustomerDTO> result = service.findAllPaged(name, pageRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());

        Mockito.verify(repository, Mockito.times(1)).find(name, pageRequest);
        Mockito.verify(mapper, Mockito.times(1)).toDTO(customer);
    }

    @Test
    public void findByIdShouldReturnCustomerDetailsDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));

        CustomerDetailsDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(customer.getName(), result.getName());

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
        Mockito.verify(mapper, Mockito.times(1)).toDetailsDTO(customer);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
        Mockito.verify(mapper, Mockito.never()).toDetailsDTO(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void findByIdShouldNotUseToDTOBecauseUsesDetailsDTO() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));

        service.findById(existingId);

        Mockito.verify(mapper, Mockito.times(1)).toDetailsDTO(customer);
        Mockito.verify(mapper, Mockito.never()).toDTO(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void insertShouldSaveCustomerAndReturnCustomerDTO() {
        Mockito.when(repository.save(ArgumentMatchers.any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerDTO result = service.insert(insertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(insertDTO.getName(), result.getName());
        Assertions.assertEquals(insertDTO.getCpf(), result.getCpf());

        Mockito.verify(mapper, Mockito.times(1)).toEntity(insertDTO);
        Mockito.verify(repository, Mockito.times(1)).save(ArgumentMatchers.any(Customer.class));
        Mockito.verify(mapper, Mockito.times(1)).toDTO(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void insertShouldSetCreatedByAsSystemWhenUserIsNotAuthenticated() {
        Mockito.when(repository.save(ArgumentMatchers.any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert(insertDTO);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(repository).save(captor.capture());

        Assertions.assertEquals("SYSTEM", captor.getValue().getCreatedBy());
    }

    @Test
    public void insertShouldSetCreatedByWithAuthenticatedUsername() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("renan", null, Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(repository.save(ArgumentMatchers.any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.insert(insertDTO);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(repository).save(captor.capture());

        Assertions.assertEquals("renan", captor.getValue().getCreatedBy());
    }

    @Test
    public void updateShouldUpdateCustomerAndReturnCustomerDTOWhenIdExists() {
        Mockito.when(repository.getOne(existingId)).thenReturn(customer);
        Mockito.when(repository.save(ArgumentMatchers.any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals(updateDTO.getName(), result.getName());
        Assertions.assertEquals(updateDTO.getEmail(), result.getEmail());

        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
        Mockito.verify(mapper, Mockito.times(1)).updateEntity(customer, updateDTO);
        Mockito.verify(repository, Mockito.times(1)).save(customer);
        Mockito.verify(mapper, Mockito.times(1)).toDTO(customer);
    }

    @Test
    public void updateShouldSetUpdatedByAsSystemWhenUserIsNotAuthenticated() {
        Mockito.when(repository.getOne(existingId)).thenReturn(customer);
        Mockito.when(repository.save(ArgumentMatchers.any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.update(existingId, updateDTO);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(repository).save(captor.capture());

        Assertions.assertEquals("SYSTEM", captor.getValue().getUpdatedBy());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, updateDTO);
        });

        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void updatePhotoShouldUpdatePhotoWhenValidFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));
        Mockito.when(repository.save(customer)).thenReturn(customer);

        service.updatePhoto(existingId, file);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(repository).save(captor.capture());

        Assertions.assertNotNull(captor.getValue().getPhoto());
        Assertions.assertEquals("image/jpeg", captor.getValue().getPhotoContentType());
    }

    @Test
    public void updatePhotoShouldThrowResourceNotFoundExceptionWhenCustomerDoesNotExist() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.updatePhoto(nonExistingId, file);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void updatePhotoShouldThrowIllegalArgumentExceptionWhenFileTypeIsInvalid() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.gif",
                "image/gif",
                "fake-image-content".getBytes()
        );

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.updatePhoto(existingId, file);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void findEntityByIdShouldReturnCustomerWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));

        Customer result = service.findEntityById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findEntityByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findEntityById(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void findEntityByIdShouldNotUseMapperBecauseReturnsEntity() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));

        service.findEntityById(existingId);

        Mockito.verify(mapper, Mockito.never()).toDTO(ArgumentMatchers.any(Customer.class));
        Mockito.verify(mapper, Mockito.never()).toDetailsDTO(ArgumentMatchers.any(Customer.class));
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldNotCallFindByIdBeforeDelete() {
        Mockito.doNothing().when(repository).deleteById(existingId);

        service.delete(existingId);

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
        Mockito.verify(repository, Mockito.never()).findById(ArgumentMatchers.anyLong());
    }

    @Test
    public void deleteAllShouldDeleteAllCustomersWhenAllIdsExist() {
        List<Long> ids = List.of(1L, 2L);

        Customer customer1 = CustomerFactory.createCustomer(1L);
        Customer customer2 = CustomerFactory.createCustomer(2L);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(customer1, customer2));

        service.deleteAll(ids);

        Mockito.verify(repository, Mockito.times(1)).findAllById(ids);
        Mockito.verify(repository, Mockito.times(1)).deleteAllByIds(ids);
    }

    @Test
    public void deleteAllShouldThrowIllegalArgumentExceptionWhenIdsIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.deleteAll(null);
        });

        Mockito.verify(repository, Mockito.never()).findAllById(ArgumentMatchers.anyList());
        Mockito.verify(repository, Mockito.never()).deleteAllByIds(ArgumentMatchers.anyList());
    }

    @Test
    public void deleteAllShouldThrowResourceNotFoundExceptionWhenSomeIdDoesNotExist() {
        List<Long> ids = List.of(1L, 2L);

        Customer customer1 = CustomerFactory.createCustomer(1L);

        Mockito.when(repository.findAllById(ids)).thenReturn(List.of(customer1));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteAll(ids);
        });

        Mockito.verify(repository, Mockito.times(1)).findAllById(ids);
        Mockito.verify(repository, Mockito.never()).deleteAllByIds(ArgumentMatchers.anyList());
    }

    @Test
    public void changeActiveStatusShouldUpdateStatusWhenIdExists() {
        boolean active = false;

        Mockito.when(repository.updateActiveById(existingId, active)).thenReturn(1);

        Assertions.assertDoesNotThrow(() -> {
            service.changeActiveStatus(existingId, active);
        });

        Mockito.verify(repository, Mockito.times(1)).updateActiveById(existingId, active);
    }

    @Test
    public void changeActiveStatusShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        boolean active = true;

        Mockito.when(repository.updateActiveById(nonExistingId, active)).thenReturn(0);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.changeActiveStatus(nonExistingId, active);
        });

        Mockito.verify(repository, Mockito.times(1)).updateActiveById(nonExistingId, active);
    }

    @Test
    public void changeActiveStatusShouldThrowRuntimeExceptionWhenDatabaseErrorOccurs() {
        boolean active = true;

        Mockito.when(repository.updateActiveById(existingId, active))
                .thenThrow(new DataAccessException("Database error") {});

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.changeActiveStatus(existingId, active);
        });

        Mockito.verify(repository, Mockito.times(1)).updateActiveById(existingId, active);
    }
}