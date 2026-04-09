package com.locadora_rdt_backend.modules.employees.positions.service;

import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import com.locadora_rdt_backend.modules.employees.positions.repository.PositionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Transactional(readOnly = true)
    public Page<PositionDTO> findAllPaged(String name, PageRequest pageRequest) {
        Page<Position> list = positionRepository.find(name, pageRequest);
        Page<PositionDTO> listDto = list.map(position -> new PositionDTO(position));

        return listDto;
    }

    @Transactional
    public PositionDTO insert(PositionDTO dto) {
        Position entity = new Position();
        entity.setName(dto.getName());
        entity = positionRepository.save(entity);
        return new PositionDTO(entity);
    }
}
