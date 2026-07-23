package com.locadora_rdt_backend.modules.stocks.stockbalances.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.stocks.stockbalances.constants.StockBalanceErrorMessages;
import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceUpdateDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.mapper.StockBalanceMapper;
import com.locadora_rdt_backend.modules.stocks.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.stocks.stockbalances.repository.StockBalanceRepository;
import com.locadora_rdt_backend.modules.rentals.rental.repository.ItemUnitRepository;
import com.locadora_rdt_backend.modules.rentals.rental.repository.RentalItemUnitRepository;
import com.locadora_rdt_backend.modules.rentals.rental.model.ItemUnit;
import com.locadora_rdt_backend.modules.rentals.rental.model.RentalItemUnitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class StockBalanceServiceImpl implements StockBalanceService {

    private final StockBalanceRepository repository;
    private final StockBalanceMapper mapper;
    private final AuthenticationFacade authenticationFacade;
    private final ItemUnitRepository itemUnitRepository;
    private final RentalItemUnitRepository rentalItemUnitRepository;

    public StockBalanceServiceImpl(
            StockBalanceRepository repository,
            StockBalanceMapper mapper,
            AuthenticationFacade authenticationFacade,
            ItemUnitRepository itemUnitRepository,
            RentalItemUnitRepository rentalItemUnitRepository
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
        this.itemUnitRepository = itemUnitRepository;
        this.rentalItemUnitRepository = rentalItemUnitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockBalanceDTO> findAllPaged(String name, PageRequest pageRequest) {
        Page<StockBalance> balances = repository.find(normalizeName(name), pageRequest);
        for (StockBalance balance : balances.getContent()) {
            synchronizeBalance(balance);
        }
        return balances.map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StockBalanceDetailsDTO findById(Long id) {
        StockBalance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockBalanceErrorMessages.STOCK_BALANCE_NOT_FOUND
                ));

        synchronizeBalance(entity);
        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public StockBalanceDetailsDTO findByItemId(Long itemId) {
        StockBalance entity = repository.findByItemId(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockBalanceErrorMessages.STOCK_BALANCE_NOT_FOUND
                ));

        synchronizeBalance(entity);
        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional
    public StockBalanceDTO updateMinimum(Long id, Integer minimumQuantity) {
        StockBalance entity = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockBalanceErrorMessages.STOCK_BALANCE_NOT_FOUND
                ));
        entity.setMinimumQuantity(minimumQuantity);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        synchronizeBalance(entity);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public StockBalanceDTO update(Long id, StockBalanceUpdateDTO dto) {
        StockBalance entity = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockBalanceErrorMessages.STOCK_BALANCE_NOT_FOUND
                ));

        validateQuantities(dto);
        updatePhysicalUnits(entity, dto);
        entity.setMinimumQuantity(dto.getMinimumQuantity());
        synchronizeBalance(entity);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    private void validateQuantities(StockBalanceUpdateDTO dto) {
        int usedQuantity = dto.getReservedQuantity() + dto.getUnavailableQuantity();
        if (usedQuantity > dto.getTotalQuantity()) {
            throw new IllegalArgumentException(
                    "A quantidade alugada e indisponível não pode ser maior que a quantidade total.");
        }
    }

    private void updatePhysicalUnits(StockBalance balance, StockBalanceUpdateDTO dto) {
        Long itemId = balance.getItem().getId();
        List<ItemUnit> units = itemUnitRepository.findActiveByItemIdForUpdate(itemId);
        int currentTotal = units.size();

        if (dto.getTotalQuantity() > currentTotal) {
            createUnits(balance, dto.getTotalQuantity() - currentTotal);
            units = itemUnitRepository.findActiveByItemIdForUpdate(itemId);
        }

        updateReservedUnits(units, dto.getReservedQuantity());
        updateUnavailableUnits(units, dto.getUnavailableQuantity());

        units = itemUnitRepository.findActiveByItemIdForUpdate(itemId);
        int quantityToRemove = units.size() - dto.getTotalQuantity();
        if (quantityToRemove > 0) {
            deactivateAvailableUnits(units, quantityToRemove);
        }
    }

    private void createUnits(StockBalance balance, int quantity) {
        String username = authenticationFacade.getAuthenticatedUsername();
        for (int index = 0; index < quantity; index++) {
            ItemUnit unit = new ItemUnit();
            unit.setItem(balance.getItem());
            unit.setAssetCode("ITEM-" + balance.getItem().getId() + "-"
                    + UUID.randomUUID().toString().substring(0, 8));
            unit.setStatus("AVAILABLE");
            unit.setConditionStatus("GOOD");
            unit.setPurchaseDate(LocalDate.now());
            unit.setNotes("Unidade criada pela edição manual do saldo.");
            unit.setActive(true);
            unit.setCreatedBy(username);
            itemUnitRepository.save(unit);
        }
    }

    private void updateReservedUnits(List<ItemUnit> units, int desiredQuantity) {
        int currentQuantity = countStatus(units, "RESERVED");
        if (desiredQuantity > currentQuantity) {
            changeAvailableStatus(units, "RESERVED", desiredQuantity - currentQuantity);
            return;
        }
        if (desiredQuantity < currentQuantity) {
            releaseReservedUnits(units, currentQuantity - desiredQuantity);
        }
    }

    private void updateUnavailableUnits(List<ItemUnit> units, int desiredQuantity) {
        int currentQuantity = countUnavailable(units);
        if (desiredQuantity > currentQuantity) {
            changeAvailableStatus(units, "MAINTENANCE", desiredQuantity - currentQuantity);
            return;
        }
        if (desiredQuantity < currentQuantity) {
            releaseMaintenanceUnits(units, currentQuantity - desiredQuantity);
        }
    }

    private void changeAvailableStatus(List<ItemUnit> units, String newStatus, int quantity) {
        int changed = 0;
        String username = authenticationFacade.getAuthenticatedUsername();
        for (ItemUnit unit : units) {
            if (changed < quantity && "AVAILABLE".equals(unit.getStatus())) {
                unit.setStatus(newStatus);
                unit.setUpdatedBy(username);
                itemUnitRepository.save(unit);
                changed++;
            }
        }
        if (changed < quantity) {
            throw new IllegalArgumentException("Não existem unidades disponíveis suficientes para esta alteração.");
        }
    }

    private void releaseReservedUnits(List<ItemUnit> units, int quantity) {
        int changed = 0;
        String username = authenticationFacade.getAuthenticatedUsername();
        List<RentalItemUnitStatus> activeStatuses = Arrays.asList(
                RentalItemUnitStatus.RESERVED, RentalItemUnitStatus.DELIVERED);
        for (ItemUnit unit : units) {
            boolean linked = rentalItemUnitRepository.existsByItemUnitIdAndStatusIn(unit.getId(), activeStatuses);
            if (changed < quantity && "RESERVED".equals(unit.getStatus()) && !linked) {
                unit.setStatus("AVAILABLE");
                unit.setUpdatedBy(username);
                itemUnitRepository.save(unit);
                changed++;
            }
        }
        if (changed < quantity) {
            throw new IllegalArgumentException("Não é possível liberar unidades alugadas por uma locação ativa.");
        }
    }

    private void releaseMaintenanceUnits(List<ItemUnit> units, int quantity) {
        int changed = 0;
        String username = authenticationFacade.getAuthenticatedUsername();
        for (ItemUnit unit : units) {
            if (changed < quantity && "MAINTENANCE".equals(unit.getStatus())) {
                unit.setStatus("AVAILABLE");
                unit.setUpdatedBy(username);
                itemUnitRepository.save(unit);
                changed++;
            }
        }
        if (changed < quantity) {
            throw new IllegalArgumentException("Não é possível alterar unidades alugadas ou vinculadas a uma locação.");
        }
    }

    private void deactivateAvailableUnits(List<ItemUnit> units, int quantity) {
        int changed = 0;
        String username = authenticationFacade.getAuthenticatedUsername();
        for (ItemUnit unit : units) {
            if (changed < quantity && "AVAILABLE".equals(unit.getStatus())) {
                unit.setActive(false);
                unit.setUpdatedBy(username);
                itemUnitRepository.save(unit);
                changed++;
            }
        }
        if (changed < quantity) {
            throw new IllegalArgumentException("Somente unidades disponíveis podem ser removidas do estoque.");
        }
    }

    private int countStatus(List<ItemUnit> units, String status) {
        int quantity = 0;
        for (ItemUnit unit : units) {
            if (status.equals(unit.getStatus())) {
                quantity++;
            }
        }
        return quantity;
    }

    private int countUnavailable(List<ItemUnit> units) {
        int quantity = 0;
        for (ItemUnit unit : units) {
            if (!"AVAILABLE".equals(unit.getStatus()) && !"RESERVED".equals(unit.getStatus())) {
                quantity++;
            }
        }
        return quantity;
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private void synchronizeBalance(StockBalance balance) {
        Long itemId = balance.getItem().getId();
        int total = (int) itemUnitRepository.countByItemIdAndActiveTrue(itemId);
        int reserved = (int) itemUnitRepository.countByItemIdAndStatusAndActiveTrue(itemId, "RESERVED");
        int unavailable = (int) itemUnitRepository.countUnavailableByItemId(itemId);
        balance.setTotalQuantity(total);
        balance.setReservedQuantity(reserved);
        balance.setUnavailableQuantity(unavailable);
    }
}
