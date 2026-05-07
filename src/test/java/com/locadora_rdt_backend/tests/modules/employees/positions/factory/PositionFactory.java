package com.locadora_rdt_backend.tests.modules.employees.positions.factory;

import com.locadora_rdt_backend.modules.employees.positions.dto.*;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;

import java.time.Instant;

public class PositionFactory {

    public static Position createPosition() {
        Position position = new Position();
        position.setId(1L);
        position.setName("Desenvolvedor Java");
        position.setCreatedAt(Instant.now());
        position.setUpdatedAt(Instant.now());
        position.setCreatedBy("admin");
        position.setUpdatedBy("admin");
        return position;
    }

    public static Position createPosition(Long id, String name) {
        Position position = new Position();
        position.setId(id);
        position.setName(name);
        position.setCreatedAt(Instant.now());
        position.setUpdatedAt(Instant.now());
        position.setCreatedBy("admin");
        position.setUpdatedBy("admin");
        return position;
    }

    public static PositionDTO createPositionDTO() {
        return new PositionDTO(1L, "Desenvolvedor Java");
    }

    public static PositionDTO createPositionDTO(Position position) {
        return new PositionDTO(position.getId(), position.getName());
    }

    public static PositionDetailsDTO createPositionDetailsDTO() {
        PositionDetailsDTO dto = new PositionDetailsDTO();
        dto.setId(1L);
        dto.setName("Desenvolvedor Java");
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());
        dto.setCreatedBy("admin");
        dto.setUpdatedBy("admin");
        return dto;
    }

    public static PositionDetailsDTO createPositionDetailsDTO(Position position) {
        PositionDetailsDTO dto = new PositionDetailsDTO();
        dto.setId(position.getId());
        dto.setName(position.getName());
        dto.setCreatedAt(position.getCreatedAt());
        dto.setUpdatedAt(position.getUpdatedAt());
        dto.setCreatedBy(position.getCreatedBy());
        dto.setUpdatedBy(position.getUpdatedBy());
        return dto;
    }

    public static PositionInsertDTO createPositionInsertDTO() {
        PositionInsertDTO dto = new PositionInsertDTO();
        dto.setName("Desenvolvedor Java");
        return dto;
    }

    public static PositionUpdateDTO createPositionUpdateDTO() {
        PositionUpdateDTO dto = new PositionUpdateDTO();
        dto.setName("Desenvolvedor Senior");
        return dto;
    }
}