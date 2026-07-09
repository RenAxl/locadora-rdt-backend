package com.locadora_rdt_backend.modules.inventory.stockmovements.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.inventory.items.service.ItemService;
import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockbalances.repository.StockBalanceRepository;
import com.locadora_rdt_backend.modules.inventory.stockmovements.constants.StockMovementErrorMessages;
import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.dto.StockMovementInsertDTO;
import com.locadora_rdt_backend.modules.inventory.stockmovements.mapper.StockMovementMapper;
import com.locadora_rdt_backend.modules.inventory.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.modules.inventory.stockmovements.repository.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    private static final String ENTRY = "ENTRY";
    private static final String EXIT = "EXIT";
    private static final String RESERVE = "RESERVE";
    private static final String RETURN = "RETURN";
    private static final String ADJUSTMENT = "ADJUSTMENT";

    private final StockMovementRepository repository;
    private final StockBalanceRepository stockBalanceRepository;
    private final ItemService itemService;
    private final StockMovementMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public StockMovementServiceImpl(
            StockMovementRepository repository,
            StockBalanceRepository stockBalanceRepository,
            ItemService itemService,
            StockMovementMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.stockBalanceRepository = stockBalanceRepository;
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

        applyMovement(balance, type, dto.getQuantity());

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

    private void applyMovement(StockBalance balance, String type, Integer quantity) {
        Integer total = zeroIfNull(balance.getTotalQuantity());
        Integer reserved = zeroIfNull(balance.getReservedQuantity());
        Integer unavailable = zeroIfNull(balance.getUnavailableQuantity());

        if (ENTRY.equals(type)) {
            balance.setTotalQuantity(total + quantity);
            return;
        }

        if (EXIT.equals(type)) {
            if (available(total, reserved, unavailable) < quantity) {
                throw new DatabaseException(StockMovementErrorMessages.INVALID_QUANTITY);
            }
            balance.setTotalQuantity(total - quantity);
            return;
        }

        if (RESERVE.equals(type)) {
            if (available(total, reserved, unavailable) < quantity) {
                throw new DatabaseException(StockMovementErrorMessages.INVALID_QUANTITY);
            }
            balance.setReservedQuantity(reserved + quantity);
            return;
        }

        if (RETURN.equals(type)) {
            if (reserved < quantity) {
                throw new DatabaseException(StockMovementErrorMessages.INVALID_QUANTITY);
            }
            balance.setReservedQuantity(reserved - quantity);
            return;
        }

        if (ADJUSTMENT.equals(type)) {
            if (quantity < reserved + unavailable) {
                throw new DatabaseException(StockMovementErrorMessages.INVALID_QUANTITY);
            }
            balance.setTotalQuantity(quantity);
            return;
        }

        throw new DatabaseException(StockMovementErrorMessages.INVALID_MOVEMENT_TYPE);
    }

    private Integer available(Integer total, Integer reserved, Integer unavailable) {
        return total - reserved - unavailable;
    }

    private Integer zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }

    private String normalizeType(String type) {
        return type == null ? "" : type.trim().toUpperCase();
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
