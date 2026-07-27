package com.locadora_rdt_backend.modules.stocks.stockmovements.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import com.locadora_rdt_backend.modules.stocks.items.service.ItemService;
import com.locadora_rdt_backend.modules.stocks.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.stocks.stockbalances.repository.StockBalanceRepository;
import com.locadora_rdt_backend.modules.stocks.stockmovements.constants.StockMovementErrorMessages;
import com.locadora_rdt_backend.modules.stocks.stockmovements.dto.StockMovementDTO;
import com.locadora_rdt_backend.modules.stocks.stockmovements.dto.StockMovementInsertDTO;
import com.locadora_rdt_backend.modules.stocks.stockmovements.mapper.StockMovementMapper;
import com.locadora_rdt_backend.modules.stocks.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.modules.stocks.stockmovements.repository.StockMovementRepository;
import com.locadora_rdt_backend.modules.rentals.rental.model.ItemUnit;
import com.locadora_rdt_backend.modules.rentals.rental.repository.ItemUnitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    private static final String ENTRY = "ENTRY";
    private static final String EXIT = "EXIT";
    private static final String RESERVE = "RESERVE";
    private static final String RETURN = "RETURN";
    private static final String ADJUSTMENT = "ADJUSTMENT";

    private final StockMovementRepository repository;
    private final StockBalanceRepository stockBalanceRepository;
    private final ItemUnitRepository itemUnitRepository;
    private final ItemService itemService;
    private final StockMovementMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public StockMovementServiceImpl(
            StockMovementRepository repository,
            StockBalanceRepository stockBalanceRepository,
            ItemUnitRepository itemUnitRepository,
            ItemService itemService,
            StockMovementMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.stockBalanceRepository = stockBalanceRepository;
        this.itemUnitRepository = itemUnitRepository;
        this.itemService = itemService;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(normalizeName(name), pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StockMovementDTO findById(Long id) {
        StockMovement entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockMovementErrorMessages.STOCK_MOVEMENT_NOT_FOUND
                ));

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public StockMovementDTO insert(StockMovementInsertDTO dto) {
        Item item = itemService.findEntityById(dto.getItemId());
        StockBalance balance = getOrCreateBalance(item);
        String type = normalizeType(dto.getType());

        applyMovement(balance, item, type, dto.getQuantity());

        StockMovement entity = mapper.toEntity(dto);
        entity.setItem(item);
        entity.setType(type);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());

        stockBalanceRepository.save(balance);
        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    private StockBalance getOrCreateBalance(Item item) {
        return stockBalanceRepository.findByItemId(item.getId())
                .orElseGet(() -> {
                    StockBalance balance = new StockBalance();
                    balance.setItem(item);
                    balance.setTotalQuantity(0);
                    balance.setReservedQuantity(0);
                    balance.setUnavailableQuantity(0);
                    balance.setMinimumQuantity(0);
                    balance.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
                    return balance;
                });
    }

    private void applyMovement(StockBalance balance, Item item, String type, Integer quantity) {
        if (ENTRY.equals(type)) {
            createAvailableUnits(item, quantity);
            synchronizeBalance(balance, item.getId());
            return;
        }

        if (EXIT.equals(type)) {
            removeAvailableUnits(item.getId(), quantity);
            synchronizeBalance(balance, item.getId());
            return;
        }

        if (RESERVE.equals(type)) {
            changeUnitStatus(item.getId(), AVAILABLE, RESERVED, quantity);
            synchronizeBalance(balance, item.getId());
            return;
        }

        if (RETURN.equals(type)) {
            changeUnitStatus(item.getId(), RESERVED, AVAILABLE, quantity);
            synchronizeBalance(balance, item.getId());
            return;
        }

        if (ADJUSTMENT.equals(type)) {
            int currentTotal = (int) itemUnitRepository.countByItemIdAndActiveTrue(item.getId());
            if (quantity > currentTotal) {
                createAvailableUnits(item, quantity - currentTotal);
            } else if (quantity < currentTotal) {
                removeAvailableUnits(item.getId(), currentTotal - quantity);
            }
            synchronizeBalance(balance, item.getId());
            return;
        }

        throw new DatabaseException(StockMovementErrorMessages.INVALID_MOVEMENT_TYPE);
    }

    private static final String AVAILABLE = "AVAILABLE";
    private static final String RESERVED = "RESERVED";

    private void createAvailableUnits(Item item, int quantity) {
        String username = authenticationFacade.getAuthenticatedUsername();
        for (int index = 0; index < quantity; index++) {
            ItemUnit unit = new ItemUnit();
            unit.setItem(item);
            unit.setAssetCode("ITEM-" + item.getId() + "-" + UUID.randomUUID().toString().substring(0, 8));
            unit.setStatus(AVAILABLE);
            unit.setConditionStatus("GOOD");
            unit.setPurchaseDate(LocalDate.now());
            unit.setNotes("Unidade criada por movimentação de estoque.");
            unit.setActive(true);
            unit.setCreatedBy(username);
            itemUnitRepository.save(unit);
        }
    }

    private void removeAvailableUnits(Long itemId, int quantity) {
        List<ItemUnit> units = itemUnitRepository.findByStatusForUpdate(
                itemId, AVAILABLE, PageRequest.of(0, quantity));
        if (units.size() < quantity) {
            throw new DatabaseException(StockMovementErrorMessages.INVALID_QUANTITY);
        }
        String username = authenticationFacade.getAuthenticatedUsername();
        for (ItemUnit unit : units) {
            unit.setActive(false);
            unit.setUpdatedBy(username);
            itemUnitRepository.save(unit);
        }
    }

    private void changeUnitStatus(Long itemId, String currentStatus, String newStatus, int quantity) {
        List<ItemUnit> units = itemUnitRepository.findByStatusForUpdate(
                itemId, currentStatus, PageRequest.of(0, quantity));
        if (units.size() < quantity) {
            throw new DatabaseException(StockMovementErrorMessages.INVALID_QUANTITY);
        }
        String username = authenticationFacade.getAuthenticatedUsername();
        for (ItemUnit unit : units) {
            unit.setStatus(newStatus);
            unit.setUpdatedBy(username);
            itemUnitRepository.save(unit);
        }
    }

    private void synchronizeBalance(StockBalance balance, Long itemId) {
        int total = (int) itemUnitRepository.countByItemIdAndActiveTrue(itemId);
        int reserved = (int) itemUnitRepository.countByItemIdAndStatusAndActiveTrue(itemId, RESERVED);
        int unavailable = (int) itemUnitRepository.countUnavailableByItemId(itemId);
        balance.setTotalQuantity(total);
        balance.setReservedQuantity(reserved);
        balance.setUnavailableQuantity(unavailable);
        balance.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
    }

    private String normalizeType(String type) {
        return type == null ? "" : type.trim().toUpperCase();
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
