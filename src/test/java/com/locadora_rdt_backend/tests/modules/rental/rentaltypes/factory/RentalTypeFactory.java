package com.locadora_rdt_backend.tests.modules.rental.rentaltypes.factory;

import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeDetailsDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeInsertDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.dto.RentalTypeUpdateDTO;
import com.locadora_rdt_backend.modules.rental.rentaltypes.model.RentalType;

import java.time.Instant;

public class RentalTypeFactory {

    public static RentalType createRentalType() {
        RentalType rentalType = new RentalType();
        rentalType.setId(1L);
        rentalType.setName("Locação Diária");
        rentalType.setType("DAY");
        rentalType.setActive(true);
        rentalType.setCreatedAt(Instant.now());
        rentalType.setUpdatedAt(Instant.now());
        rentalType.setCreatedBy("admin");
        rentalType.setUpdatedBy("admin");
        return rentalType;
    }

    public static RentalType createRentalType(Long id, String name, String type) {
        RentalType rentalType = new RentalType();
        rentalType.setId(id);
        rentalType.setName(name);
        rentalType.setType(type);
        rentalType.setActive(true);
        rentalType.setCreatedAt(Instant.now());
        rentalType.setUpdatedAt(Instant.now());
        rentalType.setCreatedBy("admin");
        rentalType.setUpdatedBy("admin");
        return rentalType;
    }

    public static RentalTypeDTO createRentalTypeDTO(RentalType rentalType) {
        return new RentalTypeDTO(
                rentalType.getId(),
                rentalType.getName(),
                rentalType.getType(),
                rentalType.getActive()
        );
    }

    public static RentalTypeDetailsDTO createRentalTypeDetailsDTO(RentalType rentalType) {
        RentalTypeDetailsDTO dto = new RentalTypeDetailsDTO();
        dto.setId(rentalType.getId());
        dto.setName(rentalType.getName());
        dto.setType(rentalType.getType());
        dto.setActive(rentalType.getActive());
        dto.setCreatedAt(rentalType.getCreatedAt());
        dto.setUpdatedAt(rentalType.getUpdatedAt());
        dto.setCreatedBy(rentalType.getCreatedBy());
        dto.setUpdatedBy(rentalType.getUpdatedBy());
        return dto;
    }

    public static RentalTypeInsertDTO createRentalTypeInsertDTO() {
        RentalTypeInsertDTO dto = new RentalTypeInsertDTO();
        dto.setName("Locação Diária");
        dto.setType("DAY");
        return dto;
    }

    public static RentalTypeUpdateDTO createRentalTypeUpdateDTO() {
        RentalTypeUpdateDTO dto = new RentalTypeUpdateDTO();
        dto.setName("Locação Mensal");
        dto.setType("MONTH");
        return dto;
    }
}
