package com.locadora_rdt_backend.tests.modules.inventory.stockbalances.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceUpdateDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.mapper.StockBalanceMapper;
import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockbalances.repository.StockBalanceRepository;
import com.locadora_rdt_backend.modules.inventory.stockbalances.service.StockBalanceServiceImpl;
import com.locadora_rdt_backend.tests.modules.inventory.stock.factory.StockFactory;
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
class StockBalanceServiceTests {

    @InjectMocks
    private StockBalanceServiceImpl service;

    @Mock
    private StockBalanceRepository repository;

    @Mock
    private StockBalanceMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;
    private StockBalance balance;
    private StockBalanceDTO balanceDTO;
    private StockBalanceDetailsDTO detailsDTO;
    private StockBalanceUpdateDTO updateDTO;
    private PageImpl<StockBalance> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;
        balance = StockFactory.createStockBalance();
        balanceDTO = StockFactory.createStockBalanceDTO(balance);
        detailsDTO = StockFactory.createStockBalanceDetailsDTO(balance);
        updateDTO = StockFactory.createStockBalanceUpdateDTO();
        page = new PageImpl<>(List.of(balance));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(balance)).thenReturn(balanceDTO);

        Page<StockBalanceDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldTrimName() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.when(repository.find("Controle", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(balance)).thenReturn(balanceDTO);

        service.findAllPaged(" Controle ", pageRequest);

        Mockito.verify(repository).find("Controle", pageRequest);
    }

    @Test
    void findAllPagedShouldUseEmptyNameWhenNameIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(balance)).thenReturn(balanceDTO);

        service.findAllPaged(null, pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(balance));
        Mockito.when(mapper.toDetailsDTO(balance)).thenReturn(detailsDTO);

        StockBalanceDetailsDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    void findByItemIdShouldReturnDTOWhenItemExists() {
        Mockito.when(repository.findByItemId(existingId)).thenReturn(Optional.of(balance));
        Mockito.when(mapper.toDetailsDTO(balance)).thenReturn(detailsDTO);

        StockBalanceDetailsDTO result = service.findByItemId(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getItemId());
    }

    @Test
    void findByItemIdShouldThrowExceptionWhenItemDoesNotExist() {
        Mockito.when(repository.findByItemId(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findByItemId(nonExistingId));
    }

    @Test
    void updateShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(balance));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(balance)).thenReturn(balance);
        Mockito.when(mapper.toDTO(balance)).thenReturn(balanceDTO);

        StockBalanceDTO result = service.update(existingId, updateDTO);

        Assertions.assertNotNull(result);
        Mockito.verify(mapper).copyToEntity(updateDTO, balance);
    }

    @Test
    void updateShouldSetUpdatedBy() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(balance));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(repository.save(balance)).thenReturn(balance);
        Mockito.when(mapper.toDTO(balance)).thenReturn(balanceDTO);

        service.update(existingId, updateDTO);

        Assertions.assertEquals("admin", balance.getUpdatedBy());
    }

    @Test
    void updateShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, updateDTO));
    }

    @Test
    void updateShouldThrowExceptionWhenTotalIsInvalid() {
        updateDTO.setTotalQuantity(2);
        updateDTO.setReservedQuantity(3);
        updateDTO.setUnavailableQuantity(1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(existingId, updateDTO));
    }
}
