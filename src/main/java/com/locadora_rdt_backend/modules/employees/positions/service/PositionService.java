package com.locadora_rdt_backend.modules.employees.positions.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.employees.positions.dto.PositionDTO;
import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import com.locadora_rdt_backend.modules.employees.positions.repository.PositionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;


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

    @Transactional(readOnly = true)
    public PositionDTO findById(Long id) {
        Position entity = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado"));
        return new PositionDTO(entity);
    }

    @Transactional
    public PositionDTO insert(PositionDTO dto) {
        Position entity = new Position();
        entity.setName(dto.getName());
        entity.setCreatedAt(Instant.now());
        entity = positionRepository.save(entity);
        return new PositionDTO(entity);
    }

    @Transactional
    public PositionDTO update(Long id, PositionDTO dto) {
        try {
            Position entity = positionRepository.getOne(id);
            entity.setName(dto.getName());
            entity.setUpdatedAt(Instant.now());
            entity = positionRepository.save(entity);

            return new PositionDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            positionRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }


}
