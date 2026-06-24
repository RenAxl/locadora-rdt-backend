package com.locadora_rdt_backend.modules.receivables.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.receivables.constants.ReceivableErrorMessages;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.receivables.repository.ReceivableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceivableServiceImpl implements ReceivableService {

    private final ReceivableRepository repository;
    private final CustomerRepository customerRepository;
    private final ReceivableMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public ReceivableServiceImpl(
            ReceivableRepository repository,
            CustomerRepository customerRepository,
            ReceivableMapper mapper,
            AuthenticationFacade authenticationFacade) {
        this.repository = repository;
        this.customerRepository = customerRepository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional
    public ReceivableDTO insert(ReceivableInsertDTO dto) {
        Receivable entity = mapper.toEntity(dto);
        validate(entity);

        entity.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    private void validate(Receivable entity) {
        boolean hasCustomer = entity.getCustomerId() != null && entity.getCustomerId() > 0L;
        boolean hasDescription = entity.getDescription() != null && !entity.getDescription().trim().isEmpty();

        if (!hasCustomer && !hasDescription) {
            throw new IllegalArgumentException(ReceivableErrorMessages.CUSTOMER_OR_DESCRIPTION_REQUIRED);
        }

        if (hasCustomer && !customerRepository.existsById(entity.getCustomerId())) {
            throw new ResourceNotFoundException(ReceivableErrorMessages.CUSTOMER_NOT_FOUND);
        }
    }
}
