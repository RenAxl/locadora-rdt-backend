package com.locadora_rdt_backend.modules.rentals.rentalhistory.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.organization.customers.model.Customer;
import com.locadora_rdt_backend.modules.organization.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.rentals.rental.model.RentalItem;
import com.locadora_rdt_backend.modules.rentals.rental.repository.RentalItemRepository;
import com.locadora_rdt_backend.modules.rentals.rentalhistory.dto.RentalHistoryDTO;
import com.locadora_rdt_backend.modules.rentals.rentalhistory.mapper.RentalHistoryMapper;
import com.locadora_rdt_backend.modules.rentals.rentalhistory.repository.RentalHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RentalHistoryServiceImpl implements RentalHistoryService {
    private final RentalHistoryRepository repository;
    private final RentalItemRepository rentalItemRepository;
    private final CustomerRepository customerRepository;
    private final RentalHistoryMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public RentalHistoryServiceImpl(RentalHistoryRepository repository,
                                    RentalItemRepository rentalItemRepository,
                                    CustomerRepository customerRepository,
                                    RentalHistoryMapper mapper,
                                    AuthenticationFacade authenticationFacade) {
        this.repository = repository;
        this.rentalItemRepository = rentalItemRepository;
        this.customerRepository = customerRepository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalHistoryDTO> findCurrentCustomerHistory(Pageable pageable) {
        String email = authenticationFacade.getAuthenticatedUsername();
        Customer customer = customerRepository.findByEmail(email);

        if (customer == null) {
            throw new ResourceNotFoundException("Cliente autenticado não encontrado.");
        }

        return repository.findByCustomerEmailIgnoreCase(email, pageable)
                .map(rental -> {
                    List<RentalItem> items = rentalItemRepository.findByRentalIdOrderById(rental.getId());
                    return mapper.toDTO(rental, items);
                });
    }
}
