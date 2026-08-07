package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import com.locadora_rdt_backend.modules.rentals.rental.model.Rental;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

@Service
public class ReceivableRentalService {

    private final ReceivableRepository repository;
    private final ReceivableRelationService relationService;
    private final ReceivableFinancialCalculator financialCalculator;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;
    private final Clock clock;

    public ReceivableRentalService(ReceivableRepository repository, ReceivableRelationService relationService,
                                   ReceivableFinancialCalculator financialCalculator,
                                   AuthenticationFacade authenticationFacade, UserRepository userRepository,
                                   Clock clock) {
        this.repository = repository;
        this.relationService = relationService;
        this.financialCalculator = financialCalculator;
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional
    public void createFromRental(Rental rental) {
        if (repository.existsByReferenceAndReferenceId(ReceivableConstants.RENTAL_REFERENCE, rental.getId())) {
            return;
        }

        LocalDate paymentDate = LocalDate.now(clock);
        BigDecimal amount = financialCalculator.valueOrZero(rental.getRemainingAmount());
        User user = getAuthenticatedUser();
        Receivable entity = new Receivable();
        entity.setDescription(ReceivableConstants.RENTAL_DESCRIPTION_PREFIX + rental.getRentalNumber());
        entity.setAmount(amount);
        entity.setDueDate(paymentDate);
        entity.setPaymentDate(paymentDate);
        entity.setCustomer(rental.getCustomer());
        entity.setPaymentMethod(rental.getPaymentMethod());
        entity.setPaymentFrequency(relationService.findCashPaymentFrequency());
        entity.setReference(ReceivableConstants.RENTAL_REFERENCE);
        entity.setReferenceId(rental.getId());
        entity.setNote(ReceivableConstants.RENTAL_NOTE);
        entity.setPaid(true);
        entity.setSubtotal(amount);
        entity.setRemainingBalance(ReceivableConstants.ZERO);
        entity.setCreatedBy(user);
        entity.setPaidBy(user);
        repository.save(entity);
    }

    private User getAuthenticatedUser() {
        String username = authenticationFacade.getAuthenticatedUsername();
        return username == null ? null : userRepository.findByEmail(username);
    }
}
