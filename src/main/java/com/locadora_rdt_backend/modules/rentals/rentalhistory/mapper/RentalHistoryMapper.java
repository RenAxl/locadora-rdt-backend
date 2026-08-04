package com.locadora_rdt_backend.modules.rentals.rentalhistory.mapper;

import com.locadora_rdt_backend.modules.rentals.rental.dto.RentalItemDTO;
import com.locadora_rdt_backend.modules.rentals.rental.model.Rental;
import com.locadora_rdt_backend.modules.rentals.rental.model.RentalItem;
import com.locadora_rdt_backend.modules.rentals.rentalhistory.dto.RentalHistoryDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RentalHistoryMapper {
    public RentalHistoryDTO toDTO(Rental rental, List<RentalItem> items) {
        RentalHistoryDTO dto = new RentalHistoryDTO();
        dto.setId(rental.getId());
        dto.setRentalNumber(rental.getRentalNumber());
        dto.setRentalTypeName(rental.getRentalType().getName());
        dto.setStatus(rental.getStatus());
        dto.setRentalDate(rental.getRentalDate());
        dto.setStartDate(rental.getStartDate());
        dto.setExpectedReturnDate(rental.getExpectedReturnDate());
        dto.setActualReturnDate(rental.getActualReturnDate());
        dto.setTotalAmount(rental.getTotalAmount());
        dto.setPaid(Boolean.TRUE.equals(rental.getPaid()) || "DELIVERED".equals(rental.getStatus()));
        dto.setItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));
        return dto;
    }

    private RentalItemDTO toItemDTO(RentalItem item) {
        RentalItemDTO dto = new RentalItemDTO();
        dto.setId(item.getId());
        dto.setItemId(item.getItem().getId());
        dto.setItemName(item.getItem().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscount());
        dto.setAdditionalFee(item.getAdditionalFee());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
