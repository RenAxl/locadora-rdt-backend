package com.locadora_rdt_backend.modules.positions.service;

import com.locadora_rdt_backend.modules.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionDetailsDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionInsertDTO;
import com.locadora_rdt_backend.modules.positions.dto.PositionUpdateDTO;
import com.locadora_rdt_backend.modules.positions.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface PositionService {

    Page<PositionDTO> findAllPaged(
            String name,
            PageRequest pageRequest
    );

    PositionDetailsDTO findById(Long id);

    Position findEntityById(Long id);

    PositionDTO insert(PositionInsertDTO dto);

    PositionDTO update(
            Long id,
            PositionUpdateDTO dto
    );

    void delete(Long id);
}
