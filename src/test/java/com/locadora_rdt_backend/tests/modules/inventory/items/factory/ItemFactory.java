package com.locadora_rdt_backend.tests.modules.inventory.items.factory;

import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemDetailsDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemInsertDTO;
import com.locadora_rdt_backend.modules.stocks.items.dto.ItemUpdateDTO;
import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import com.locadora_rdt_backend.modules.stocks.categories.model.Category;
import com.locadora_rdt_backend.tests.modules.rental.categories.factory.CategoryFactory;

import java.math.BigDecimal;
import java.time.Instant;

public class ItemFactory {

    public static Item createItem() {
        Category category = CategoryFactory.createCategory();

        Item item = new Item();
        item.setId(1L);
        item.setVersion(0L);
        item.setName("Playstation 5");
        item.setDescription("Console PlayStation 5 Slim para locação.");
        item.setCategory(category);
        item.setPrice(new BigDecimal("120.00"));
        item.setImage(new byte[]{1});
        item.setActive(true);
        item.setCreatedAt(Instant.now());
        item.setUpdatedAt(Instant.now());
        item.setCreatedBy("admin");
        item.setUpdatedBy("admin");
        return item;
    }

    public static ItemDTO createItemDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setActive(item.getActive());
        dto.setCategory(CategoryFactory.createCategoryDTO(item.getCategory()));
        return dto;
    }

    public static ItemDetailsDTO createItemDetailsDTO(Item item) {
        ItemDetailsDTO dto = new ItemDetailsDTO();
        dto.setId(item.getId());
        dto.setVersion(item.getVersion());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setActive(item.getActive());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        dto.setCreatedBy(item.getCreatedBy());
        dto.setUpdatedBy(item.getUpdatedBy());
        dto.setCategory(CategoryFactory.createCategoryDTO(item.getCategory()));
        return dto;
    }

    public static ItemInsertDTO createItemInsertDTO() {
        ItemInsertDTO dto = new ItemInsertDTO();
        dto.setName("Playstation 5");
        dto.setDescription("Console PlayStation 5 Slim para locação.");
        dto.setCategoryId(1L);
        dto.setPrice(new BigDecimal("120.00"));
        return dto;
    }

    public static ItemUpdateDTO createItemUpdateDTO() {
        ItemUpdateDTO dto = new ItemUpdateDTO();
        dto.setName("Xbox Series X");
        dto.setDescription("Console Xbox Series X para locação.");
        dto.setCategoryId(1L);
        dto.setPrice(new BigDecimal("100.00"));
        return dto;
    }
}
