package com.locadora_rdt_backend.modules.rentals.rentaltypes.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.model.RentalType;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.constants.RentalTypeErrorMessages;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.dto.RentalTypeDTO;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.dto.RentalTypeDetailsDTO;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.dto.RentalTypeInsertDTO;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.dto.RentalTypeUpdateDTO;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.mapper.RentalTypeMapper;
import com.locadora_rdt_backend.modules.rentals.rentaltypes.repository.RentalTypeRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalTypeServiceImpl implements RentalTypeService {

    private final RentalTypeRepository repository;
    private final RentalTypeMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public RentalTypeServiceImpl(
            RentalTypeRepository repository,
            RentalTypeMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalTypeDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(normalizeName(name), pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalTypeDetailsDTO findById(Long id) {
        RentalType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RentalTypeErrorMessages.RENTAL_TYPE_NOT_FOUND
                ));

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalType findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RentalTypeErrorMessages.RENTAL_TYPE_NOT_FOUND
                ));
    }

    @Override
    @Transactional
    public RentalTypeDTO insert(RentalTypeInsertDTO dto) {
        RentalType entity = mapper.toEntity(dto);

        entity.setActive(true);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public RentalTypeDTO update(Long id, RentalTypeUpdateDTO dto) {
        RentalType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RentalTypeErrorMessages.RENTAL_TYPE_NOT_FOUND
                ));

        mapper.copyToEntity(dto, entity);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RentalType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        RentalTypeErrorMessages.RENTAL_TYPE_NOT_FOUND
                ));

        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    RentalTypeErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    @Override
    @Transactional
    public void deleteAll(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Lista de ids vazia");
        }

        List<Long> existingIds = repository.findAllById(ids)
                .stream()
                .map(RentalType::getId)
                .collect(Collectors.toList());

        if (existingIds.size() != ids.size()) {
            throw new ResourceNotFoundException("Um ou mais IDs não existem");
        }

        try {
            repository.deleteAllByIds(ids);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    RentalTypeErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    @Override
    @Transactional
    public void changeActiveStatus(Long id, boolean active) {
        try {
            int updated = repository.updateActiveById(id, active);

            if (updated == 0) {
                throw new ResourceNotFoundException(
                        RentalTypeErrorMessages.RENTAL_TYPE_NOT_FOUND
                );
            }
        } catch (DataAccessException e) {
            throw new DatabaseException("Error changing rental type status.");
        }
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
