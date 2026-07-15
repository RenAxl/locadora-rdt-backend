package com.locadora_rdt_backend.modules.rental.mapper;

import com.locadora_rdt_backend.modules.rental.dto.*;
import com.locadora_rdt_backend.modules.rental.model.Rental;
import com.locadora_rdt_backend.modules.rental.model.RentalItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RentalMapper {
    public RentalDTO toDTO(Rental rental) {
        RentalDTO dto = new RentalDTO();
        copyBasic(rental, dto);
        return dto;
    }

    public RentalDetailsDTO toDetailsDTO(Rental rental, List<RentalItem> items) {
        RentalDetailsDTO dto = new RentalDetailsDTO();
        copyBasic(rental, dto);
        if (rental.getPaymentMethod() != null) {
            dto.setPaymentMethodId(rental.getPaymentMethod().getId());
            dto.setPaymentMethodName(rental.getPaymentMethod().getName());
        }
        dto.setSubtotal(rental.getSubtotal());
        dto.setDiscount(rental.getDiscount());
        dto.setShippingFee(rental.getShippingFee());
        dto.setAdditionalFee(rental.getAdditionalFee());
        dto.setDownPayment(rental.getDownPayment());
        dto.setRemainingAmount(rental.getRemainingAmount());
        dto.setDeliveryAddress(rental.getDeliveryAddress());
        dto.setNotes(rental.getNotes());
        dto.setCreatedAt(rental.getCreatedAt());
        dto.setUpdatedAt(rental.getUpdatedAt());
        dto.setCreatedBy(rental.getCreatedBy());
        dto.setUpdatedBy(rental.getUpdatedBy());
        dto.setItems(items.stream().map(this::toItemDTO).collect(Collectors.toList()));
        return dto;
    }

    private void copyBasic(Rental rental, RentalDTO dto) {
        dto.setId(rental.getId());
        dto.setRentalNumber(rental.getRentalNumber());
        dto.setCustomerId(rental.getCustomer().getId());
        dto.setCustomerName(rental.getCustomer().getName());
        dto.setRentalTypeId(rental.getRentalType().getId());
        dto.setRentalTypeName(rental.getRentalType().getName());
        dto.setStatus(rental.getStatus());
        dto.setRentalDate(rental.getRentalDate());
        dto.setStartDate(rental.getStartDate());
        dto.setExpectedReturnDate(rental.getExpectedReturnDate());
        dto.setTotalAmount(rental.getTotalAmount());
    }

    private RentalItemDTO toItemDTO(RentalItem entity) {
        RentalItemDTO dto = new RentalItemDTO();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItem().getId());
        dto.setItemName(entity.getItem().getName());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setDiscount(entity.getDiscount());
        dto.setAdditionalFee(entity.getAdditionalFee());
        dto.setSubtotal(entity.getSubtotal());
        return dto;
    }
}
