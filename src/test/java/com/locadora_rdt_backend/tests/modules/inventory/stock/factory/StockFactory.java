package com.locadora_rdt_backend.tests.modules.inventory.stock.factory;

import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.dto.StockBalanceUpdateDTO;
import com.locadora_rdt_backend.modules.stocks.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.stocks.stockmovements.dto.StockMovementDTO;
import com.locadora_rdt_backend.modules.stocks.stockmovements.dto.StockMovementInsertDTO;
import com.locadora_rdt_backend.modules.stocks.stockmovements.model.StockMovement;
import com.locadora_rdt_backend.tests.modules.inventory.items.factory.ItemFactory;

import java.time.Instant;

public class StockFactory {

    public static StockBalance createStockBalance() {
        StockBalance balance = new StockBalance();
        balance.setId(1L);
        balance.setVersion(0L);
        balance.setItem(ItemFactory.createItem());
        balance.setTotalQuantity(10);
        balance.setReservedQuantity(2);
        balance.setUnavailableQuantity(1);
        balance.setMinimumQuantity(3);
        balance.setCreatedAt(Instant.parse("2026-07-01T10:00:00Z"));
        balance.setUpdatedAt(Instant.parse("2026-07-02T10:00:00Z"));
        balance.setCreatedBy("admin");
        balance.setUpdatedBy("admin");
        return balance;
    }

    public static StockBalanceDTO createStockBalanceDTO(StockBalance balance) {
        StockBalanceDTO dto = new StockBalanceDTO();
        dto.setId(balance.getId());
        dto.setItemId(balance.getItem().getId());
        dto.setItemName(balance.getItem().getName());
        dto.setTotalQuantity(balance.getTotalQuantity());
        dto.setReservedQuantity(balance.getReservedQuantity());
        dto.setUnavailableQuantity(balance.getUnavailableQuantity());
        dto.setAvailableQuantity(7);
        dto.setMinimumQuantity(balance.getMinimumQuantity());
        dto.setLowStock(false);
        return dto;
    }

    public static StockBalanceDetailsDTO createStockBalanceDetailsDTO(StockBalance balance) {
        StockBalanceDetailsDTO dto = new StockBalanceDetailsDTO();
        dto.setId(balance.getId());
        dto.setVersion(balance.getVersion());
        dto.setItemId(balance.getItem().getId());
        dto.setItemName(balance.getItem().getName());
        dto.setTotalQuantity(balance.getTotalQuantity());
        dto.setReservedQuantity(balance.getReservedQuantity());
        dto.setUnavailableQuantity(balance.getUnavailableQuantity());
        dto.setAvailableQuantity(7);
        dto.setMinimumQuantity(balance.getMinimumQuantity());
        dto.setLowStock(false);
        dto.setCreatedAt(balance.getCreatedAt());
        dto.setUpdatedAt(balance.getUpdatedAt());
        dto.setCreatedBy(balance.getCreatedBy());
        dto.setUpdatedBy(balance.getUpdatedBy());
        return dto;
    }

    public static StockBalanceUpdateDTO createStockBalanceUpdateDTO() {
        StockBalanceUpdateDTO dto = new StockBalanceUpdateDTO();
        dto.setTotalQuantity(12);
        dto.setReservedQuantity(3);
        dto.setUnavailableQuantity(2);
        dto.setMinimumQuantity(4);
        return dto;
    }

    public static StockMovement createStockMovement(String type) {
        StockMovement movement = new StockMovement();
        movement.setId(1L);
        movement.setItem(ItemFactory.createItem());
        movement.setType(type);
        movement.setQuantity(2);
        movement.setReason("Ajuste de teste");
        movement.setReferenceType("TEST");
        movement.setReferenceId(10L);
        movement.setCreatedAt(Instant.parse("2026-07-01T10:00:00Z"));
        movement.setCreatedBy("admin");
        return movement;
    }

    public static StockMovementDTO createStockMovementDTO(StockMovement movement) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setItemId(movement.getItem().getId());
        dto.setItemName(movement.getItem().getName());
        dto.setType(movement.getType());
        dto.setQuantity(movement.getQuantity());
        dto.setReason(movement.getReason());
        dto.setReferenceType(movement.getReferenceType());
        dto.setReferenceId(movement.getReferenceId());
        dto.setCreatedAt(movement.getCreatedAt());
        dto.setCreatedBy(movement.getCreatedBy());
        return dto;
    }

    public static StockMovementInsertDTO createStockMovementInsertDTO(String type, Integer quantity) {
        StockMovementInsertDTO dto = new StockMovementInsertDTO();
        dto.setItemId(1L);
        dto.setType(type);
        dto.setQuantity(quantity);
        dto.setReason("Movimento de teste");
        dto.setReferenceType("TEST");
        dto.setReferenceId(10L);
        return dto;
    }

    public static StockBalance createBalanceForItem(Item item, int total, int reserved, int unavailable) {
        StockBalance balance = new StockBalance();
        balance.setId(1L);
        balance.setItem(item);
        balance.setTotalQuantity(total);
        balance.setReservedQuantity(reserved);
        balance.setUnavailableQuantity(unavailable);
        balance.setMinimumQuantity(1);
        balance.setCreatedBy("admin");
        return balance;
    }
}
