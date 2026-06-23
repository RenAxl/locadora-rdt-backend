package com.locadora_rdt_backend.tests.modules.customers.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDetailsDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerInsertDTO;
import com.locadora_rdt_backend.modules.customers.dto.CustomerUpdateDTO;
import com.locadora_rdt_backend.modules.customers.mapper.CustomerMapper;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.customers.service.CustomerServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTests {

    @InjectMocks
    private CustomerServiceImpl service;

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;
    private Customer customer;
    private CustomerDTO customerDTO;
    private CustomerDetailsDTO detailsDTO;
    private CustomerInsertDTO insertDTO;
    private CustomerUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;
        customer = new Customer();
        customer.setId(existingId);
        customer.setName("Cliente");
        customerDTO = new CustomerDTO();
        customerDTO.setId(existingId);
        customerDTO.setName("Cliente");
        detailsDTO = new CustomerDetailsDTO();
        detailsDTO.setId(existingId);
        detailsDTO.setName("Cliente");
        insertDTO = new CustomerInsertDTO();
        insertDTO.setName("Cliente");
        updateDTO = new CustomerUpdateDTO();
        updateDTO.setName("Cliente atualizado");
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Customer> page = new PageImpl<>(List.of(customer));

        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(customer)).thenReturn(customerDTO);

        Page<CustomerDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));
        Mockito.when(mapper.toDetailsDTO(customer)).thenReturn(detailsDTO);

        CustomerDetailsDTO result = service.findById(existingId);

        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    void insertShouldReturnDTOAndSetCreatedBy() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(customer);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(customer)).thenReturn(customer);
        Mockito.when(mapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = service.insert(insertDTO);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("admin", customer.getCreatedBy());
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.getOne(existingId)).thenReturn(customer);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(customer)).thenReturn(customer);
        Mockito.when(mapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = service.update(existingId, updateDTO);

        Assertions.assertEquals(existingId, result.getId());
        Mockito.verify(mapper).updateEntity(customer, updateDTO);
    }

    @Test
    void updateShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, updateDTO));
    }

    @Test
    void updatePhotoShouldSaveValidPhoto() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");

        service.updatePhoto(existingId, file);

        Assertions.assertArrayEquals(new byte[]{1}, customer.getPhoto());
        Assertions.assertEquals("image/png", customer.getPhotoContentType());
        Mockito.verify(repository).save(customer);
    }

    @Test
    void updatePhotoShouldThrowWhenCustomerDoesNotExist() {
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1});

        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.updatePhoto(nonExistingId, file));
    }

    @Test
    void updatePhotoShouldThrowWhenFileIsInvalid() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updatePhoto(existingId, null));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                service.updatePhoto(existingId, new MockMultipartFile("file", "photo.gif", "image/gif", new byte[]{1})));
    }

    @Test
    void updatePhotoShouldThrowWhenFileReadFails() throws IOException {
        MockMultipartFile file = Mockito.mock(MockMultipartFile.class);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(file.getContentType()).thenReturn("image/png");
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.getBytes()).thenThrow(IOException.class);

        Assertions.assertThrows(RuntimeException.class, () -> service.updatePhoto(existingId, file));
    }

    @Test
    void findEntityByIdShouldReturnEntityWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(customer));

        Assertions.assertEquals(customer, service.findEntityById(existingId));
    }

    @Test
    void deleteShouldThrowWhenIdDoesNotExist() {
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(nonExistingId);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }

    @Test
    void deleteAllShouldValidateIds() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteAll(List.of()));

        Mockito.when(repository.findAllById(List.of(existingId, nonExistingId))).thenReturn(List.of(customer));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.deleteAll(List.of(existingId, nonExistingId)));
    }

    @Test
    void deleteAllShouldDeleteExistingIds() {
        Mockito.when(repository.findAllById(List.of(existingId))).thenReturn(List.of(customer));

        service.deleteAll(List.of(existingId));

        Mockito.verify(repository).deleteAllByIds(List.of(existingId));
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

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.changeActiveStatus(nonExistingId, false));
    }

    @Test
    void changeActiveStatusShouldThrowWhenDataAccessFails() {
        Mockito.when(repository.updateActiveById(existingId, false))
                .thenThrow(new DataAccessResourceFailureException("fail"));

        Assertions.assertThrows(RuntimeException.class, () -> service.changeActiveStatus(existingId, false));
    }
}
