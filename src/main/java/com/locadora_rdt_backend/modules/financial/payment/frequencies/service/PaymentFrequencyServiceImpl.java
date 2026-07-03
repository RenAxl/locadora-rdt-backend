package com.locadora_rdt_backend.modules.financial.payment.frequencies.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.constants.PaymentFrequencyErrorMessages;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyInsertDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.dto.PaymentFrequencyUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.mapper.PaymentFrequencyMapper;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.repository.PaymentFrequencyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentFrequencyServiceImpl implements PaymentFrequencyService {

    private final PaymentFrequencyRepository repository;
    private final PaymentFrequencyMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public PaymentFrequencyServiceImpl(
            PaymentFrequencyRepository repository,
            PaymentFrequencyMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentFrequencyDTO> findAllPaged(String frequency, PageRequest pageRequest) {
        return repository.find(normalizeFrequency(frequency), pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentFrequencyDetailsDTO findById(Long id) {
        PaymentFrequency entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentFrequencyErrorMessages.PAYMENT_FREQUENCY_NOT_FOUND
                ));

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional
    public PaymentFrequencyDTO insert(PaymentFrequencyInsertDTO dto) {
        PaymentFrequency entity = mapper.toEntity(dto);
        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public PaymentFrequencyDTO update(Long id, PaymentFrequencyUpdateDTO dto) {
        PaymentFrequency entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentFrequencyErrorMessages.PAYMENT_FREQUENCY_NOT_FOUND
                ));

        mapper.updateEntity(entity, dto);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentFrequency findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentFrequencyErrorMessages.PAYMENT_FREQUENCY_NOT_FOUND
                ));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PaymentFrequency entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PaymentFrequencyErrorMessages.PAYMENT_FREQUENCY_NOT_FOUND
                ));

        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    PaymentFrequencyErrorMessages.DATABASE_INTEGRITY_VIOLATION
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
                .map(PaymentFrequency::getId)
                .collect(Collectors.toList());

        if (existingIds.size() != ids.size()) {
            throw new ResourceNotFoundException("Um ou mais IDs não existem");
        }

        try {
            repository.deleteAllByIds(ids);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    PaymentFrequencyErrorMessages.DATABASE_INTEGRITY_VIOLATION
            );
        }
    }

    private String normalizeFrequency(String frequency) {
        return frequency == null ? "" : frequency.trim();
    }
}
