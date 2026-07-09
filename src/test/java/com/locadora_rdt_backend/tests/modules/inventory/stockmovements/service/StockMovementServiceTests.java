package com.locadora_rdt_backend.tests.modules.inventory.stockmovements.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.inventory.items.service.ItemService;
import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockbalances.repository.StockBalanceRepository;
import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementInsertDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.mapper.StockMovementMapper;
import com.locadora_rdt_backend.modules.inventory.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.modules.inventory.stockmovements.repository.StockMovementRepository;
import com.locadora_rdt_backend.modules.inventory.stockmovements.service.StockMovementServiceImpl;
import com.locadora_rdt_backend.tests.modules.inventory.items.factory.ItemFactory;
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
class StockMovementServiceTests {

    @InjectMocks
    private StockMovementServiceImpl service;

    @Mock
    private StockMovementRepository repository;

    @Mock
    private StockBalanceRepository stockBalanceRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private StockMovementMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private Long existingId;
    private Long nonExistingId;
    private Item item;
    private StockBalance balance;
    private StockMovement movement;
    private StockMovementDTO movementDTO;
    private PageImpl<StockMovement> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;
        item = ItemFactory.createItem();
        balance = StockFactory.createBalanceForItem(item, 10, 2, 1);
        movement = StockFactory.createStockMovement("ENTRY");
        movementDTO = StockFactory.createStockMovementDTO(movement);
        page = new PageImpl<>(List.of(movement));
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(movement)).thenReturn(movementDTO);

        Page<StockMovementDTO> result = service.findAllPaged("", pageRequest);

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllPagedShouldTrimName() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.when(repository.find("Controle", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(movement)).thenReturn(movementDTO);

        service.findAllPaged(" Controle ", pageRequest);

        Mockito.verify(repository).find("Controle", pageRequest);
    }

    @Test
    void findAllPagedShouldUseEmptyNameWhenNameIsNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.when(repository.find("", pageRequest)).thenReturn(page);
        Mockito.when(mapper.toDTO(movement)).thenReturn(movementDTO);

        service.findAllPaged(null, pageRequest);

        Mockito.verify(repository).find("", pageRequest);
    }

    @Test
    void findByIdShouldReturnDTOWhenIdExists() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(movement));
        Mockito.when(mapper.toDTO(movement)).thenReturn(movementDTO);

        StockMovementDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    void insertEntryShouldIncreaseTotalQuantity() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO(" entry ", 2);
        prepareInsert(dto);

        service.insert(dto);

        Assertions.assertEquals(12, balance.getTotalQuantity());
    }

    @Test
    void insertExitShouldDecreaseTotalQuantity() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("EXIT", 2);
        prepareInsert(dto);

        service.insert(dto);

        Assertions.assertEquals(8, balance.getTotalQuantity());
    }

    @Test
    void insertReserveShouldIncreaseReservedQuantity() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("RESERVE", 2);
        prepareInsert(dto);

        service.insert(dto);

        Assertions.assertEquals(4, balance.getReservedQuantity());
    }

    @Test
    void insertReturnShouldDecreaseReservedQuantity() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("RETURN", 1);
        prepareInsert(dto);

        service.insert(dto);

        Assertions.assertEquals(1, balance.getReservedQuantity());
    }

    @Test
    void insertAdjustmentShouldSetTotalQuantity() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("ADJUSTMENT", 20);
        prepareInsert(dto);

        service.insert(dto);

        Assertions.assertEquals(20, balance.getTotalQuantity());
    }

    @Test
    void insertShouldCreateBalanceWhenItemDoesNotHaveBalance() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("ENTRY", 5);
        StockMovement newMovement = StockFactory.createStockMovement("ENTRY");
        Mockito.when(itemService.findEntityById(dto.getItemId())).thenReturn(item);
        Mockito.when(stockBalanceRepository.findByItemId(item.getId())).thenReturn(Optional.empty());
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(mapper.toEntity(dto)).thenReturn(newMovement);
        Mockito.when(repository.save(newMovement)).thenReturn(newMovement);
        Mockito.when(mapper.toDTO(newMovement)).thenReturn(movementDTO);

        service.insert(dto);

        Mockito.verify(stockBalanceRepository).save(Mockito.argThat(savedBalance ->
                savedBalance.getTotalQuantity().equals(5)
                        && savedBalance.getReservedQuantity().equals(0)
                        && savedBalance.getUnavailableQuantity().equals(0)
                        && savedBalance.getCreatedBy().equals("admin")
        ));
    }

    @Test
    void insertShouldSetItemTypeAndCreatedBy() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO(" entry ", 2);
        prepareInsert(dto);

        service.insert(dto);

        Assertions.assertEquals(item, movement.getItem());
        Assertions.assertEquals("ENTRY", movement.getType());
        Assertions.assertEquals("admin", movement.getCreatedBy());
    }

    @Test
    void insertExitShouldThrowExceptionWhenQuantityIsInvalid() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("EXIT", 20);
        prepareInsertUntilBalance(dto);

        Assertions.assertThrows(DatabaseException.class, () -> service.insert(dto));
    }

    @Test
    void insertReserveShouldThrowExceptionWhenQuantityIsInvalid() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("RESERVE", 20);
        prepareInsertUntilBalance(dto);

        Assertions.assertThrows(DatabaseException.class, () -> service.insert(dto));
    }

    @Test
    void insertReturnShouldThrowExceptionWhenQuantityIsInvalid() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("RETURN", 20);
        prepareInsertUntilBalance(dto);

        Assertions.assertThrows(DatabaseException.class, () -> service.insert(dto));
    }

    @Test
    void insertAdjustmentShouldThrowExceptionWhenQuantityIsInvalid() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("ADJUSTMENT", 1);
        prepareInsertUntilBalance(dto);

        Assertions.assertThrows(DatabaseException.class, () -> service.insert(dto));
    }

    @Test
    void insertShouldThrowExceptionWhenTypeIsInvalid() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("INVALID", 1);
        prepareInsertUntilBalance(dto);

        Assertions.assertThrows(DatabaseException.class, () -> service.insert(dto));
    }

    @Test
    void insertShouldThrowExceptionWhenTypeIsNull() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO(null, 1);
        prepareInsertUntilBalance(dto);

        Assertions.assertThrows(DatabaseException.class, () -> service.insert(dto));
    }

    @Test
    void insertShouldUseZeroWhenBalanceQuantitiesAreNull() {
        StockMovementInsertDTO dto = StockFactory.createStockMovementInsertDTO("ENTRY", 3);
        balance.setTotalQuantity(null);
        balance.setReservedQuantity(null);
        balance.setUnavailableQuantity(null);
        prepareInsert(dto);

        service.insert(dto);

        Assertions.assertEquals(3, balance.getTotalQuantity());
    }

    private void prepareInsert(StockMovementInsertDTO dto) {
        prepareInsertUntilBalance(dto);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin");
        Mockito.when(mapper.toEntity(dto)).thenReturn(movement);
        Mockito.when(repository.save(movement)).thenReturn(movement);
        Mockito.when(mapper.toDTO(movement)).thenReturn(movementDTO);
    }

    private void prepareInsertUntilBalance(StockMovementInsertDTO dto) {
        Mockito.when(itemService.findEntityById(dto.getItemId())).thenReturn(item);
        Mockito.when(stockBalanceRepository.findByItemId(item.getId())).thenReturn(Optional.of(balance));
    }
}
