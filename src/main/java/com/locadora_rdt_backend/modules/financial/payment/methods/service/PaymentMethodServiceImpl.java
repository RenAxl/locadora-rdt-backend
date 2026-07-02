package com.locadora_rdt_backend.modules.financial.payment.methods.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payment.methods.constants.PaymentMethodErrorMessages;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodInsertDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.mapper.PaymentMethodMapper;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment.methods.repository.PaymentMethodRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public PaymentMethodServiceImpl(
            PaymentMethodRepository repository,
            PaymentMethodMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentMethodDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(normalizeName(name), pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethodDetailsDTO findById(Long id) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentMethodErrorMessages.PAYMENT_METHOD_NOT_FOUND
                ));

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional
    public PaymentMethodDTO insert(PaymentMethodInsertDTO dto) {
        PaymentMethod entity = mapper.toEntity(dto);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public PaymentMethodDTO update(Long id, PaymentMethodUpdateDTO dto) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentMethodErrorMessages.PAYMENT_METHOD_NOT_FOUND
                ));

        mapper.updateEntity(entity, dto);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethod findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentMethodErrorMessages.PAYMENT_METHOD_NOT_FOUND
                ));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentMethodErrorMessages.PAYMENT_METHOD_NOT_FOUND
                ));

        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    PaymentMethodErrorMessages.DATABASE_INTEGRITY_VIOLATION
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
                .map(PaymentMethod::getId)
                .collect(Collectors.toList());

        if (existingIds.size() != ids.size()) {
            throw new ResourceNotFoundException("Um ou mais IDs não existem");
        }

        try {
            repository.deleteAllByIds(ids);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    PaymentMethodErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }
}
