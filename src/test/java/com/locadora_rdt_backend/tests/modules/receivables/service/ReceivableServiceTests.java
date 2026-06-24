package com.locadora_rdt_backend.tests.modules.receivables.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.receivables.model.ReceivableStatus;
import com.locadora_rdt_backend.modules.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.receivables.service.ReceivableServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class ReceivableServiceTests {

    @InjectMocks
    private ReceivableServiceImpl service;

    @Mock
    private ReceivableRepository repository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ReceivableMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private ReceivableInsertDTO insertDTO;
    private Receivable receivable;
    private ReceivableDTO receivableDTO;

    @BeforeEach
    void setUp() {
        insertDTO = new ReceivableInsertDTO();
        insertDTO.setCustomerId(1L);
        insertDTO.setAmount(new BigDecimal("100.00"));
        insertDTO.setDueDate(LocalDate.of(2026, 7, 10));

        receivable = new Receivable();
        receivable.setId(1L);
        receivable.setCustomerId(1L);
        receivable.setAmount(new BigDecimal("100.00"));
        receivable.setDueDate(LocalDate.of(2026, 7, 10));
        receivable.normalizeDefaults();

        receivableDTO = new ReceivableDTO();
        receivableDTO.setId(1L);
        receivableDTO.setCustomerId(1L);
        receivableDTO.setAmount(new BigDecimal("100.00"));
        receivableDTO.setDueDate(LocalDate.of(2026, 7, 10));
        receivableDTO.setStatus(ReceivableStatus.PENDING);
    }

    @Test
    void insertShouldSavePendingReceivableWhenPaymentDateIsEmpty() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(receivable);
        Mockito.when(customerRepository.existsById(1L)).thenReturn(true);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(receivable)).thenReturn(receivable);
        Mockito.when(mapper.toDTO(receivable)).thenReturn(receivableDTO);

        ReceivableDTO result = service.insert(insertDTO);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(ReceivableStatus.PENDING, receivable.getStatus());
        Assertions.assertEquals("admin", receivable.getCreatedBy());
    }

    @Test
    void insertShouldAllowDescriptionWithoutCustomerAndNormalizeIds() {
        Receivable noCustomer = new Receivable();
        noCustomer.setDescription("Receita avulsa");
        noCustomer.setAmount(new BigDecimal("80.00"));
        noCustomer.setDueDate(LocalDate.of(2026, 7, 10));
        noCustomer.normalizeDefaults();

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(noCustomer);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(noCustomer)).thenReturn(noCustomer);
        Mockito.when(mapper.toDTO(noCustomer)).thenReturn(receivableDTO);

        service.insert(insertDTO);

        ArgumentCaptor<Receivable> captor = ArgumentCaptor.forClass(Receivable.class);
        Mockito.verify(repository).save(captor.capture());
        Assertions.assertEquals(0L, captor.getValue().getCustomerId());
        Assertions.assertEquals(0L, captor.getValue().getPaymentMethodId());
        Assertions.assertEquals(0L, captor.getValue().getFrequencyId());
        Mockito.verify(customerRepository, Mockito.never()).existsById(Mockito.anyLong());
    }

    @Test
    void insertShouldCreatePaidReceivableWhenPaymentDateIsFilled() {
        receivable.setPaymentDate(LocalDate.of(2026, 7, 11));
        receivable.normalizeDefaults();

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(receivable);
        Mockito.when(customerRepository.existsById(1L)).thenReturn(true);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(receivable)).thenReturn(receivable);
        Mockito.when(mapper.toDTO(receivable)).thenReturn(receivableDTO);

        service.insert(insertDTO);

        Assertions.assertEquals(ReceivableStatus.PAID, receivable.getStatus());
    }

    @Test
    void insertShouldThrowWhenCustomerAndDescriptionAreEmpty() {
        Receivable invalid = new Receivable();
        invalid.setAmount(new BigDecimal("100.00"));
        invalid.setDueDate(LocalDate.of(2026, 7, 10));
        invalid.normalizeDefaults();

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(invalid);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(insertDTO));
    }

    @Test
    void insertShouldThrowWhenCustomerDoesNotExist() {
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(receivable);
        Mockito.when(customerRepository.existsById(1L)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(insertDTO));
    }
}
