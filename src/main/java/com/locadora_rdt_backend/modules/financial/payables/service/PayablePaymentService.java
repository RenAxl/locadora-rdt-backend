package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payables.constants.PayableConstants;
import com.locadora_rdt_backend.modules.financial.payables.constants.PayableErrorMessages;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.payables.mapper.PayableMapper;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import com.locadora_rdt_backend.modules.financial.payment_methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

@Service
public class PayablePaymentService {

    private final PayableRepository repository;
    private final PayableMapper mapper;
    private final PayableRelationService relationService;
    private final PayableFinancialCalculator financialCalculator;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;
    private final Clock clock;

    public PayablePaymentService(
            PayableRepository repository,
            PayableMapper mapper,
            PayableRelationService relationService,
            PayableFinancialCalculator financialCalculator,
            AuthenticationFacade authenticationFacade,
            UserRepository userRepository,
            Clock clock
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.relationService = relationService;
        this.financialCalculator = financialCalculator;
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional
    public PayableDTO pay(Payable entity, PayablePaymentDTO dto) {
        BigDecimal paymentAmount = financialCalculator.valueOrZero(dto.getPaymentAmount());
        validatePayment(entity, dto, paymentAmount);

        User user = getAuthenticatedUser();
        BigDecimal amount = financialCalculator.valueOrZero(entity.getAmount());
        BigDecimal openAmount = financialCalculator.getOpenAmount(entity);
        BigDecimal paidAmount = amount.subtract(openAmount);
        PaymentMethod requestedPaymentMethod = relationService.findPaymentMethod(dto.getPaymentMethodId());
        BigDecimal paymentLimit = financialCalculator.getCurrentPaymentLimit(entity, dto);

        if (paymentAmount.compareTo(paymentLimit) == 0 || paymentAmount.compareTo(openAmount) >= 0) {
            payTotal(entity, dto, user, requestedPaymentMethod, paidAmount.add(openAmount));
            return toDTOWithLateCharges(repository.save(entity));
        }

        BigDecimal remaining = openAmount.subtract(paymentAmount);
        entity.setSubtotal(paidAmount.add(paymentAmount));
        entity.setRemainingBalance(remaining);
        entity.setPaid(false);
        entity.setPaymentDate(dto.getPaymentDate() == null ? today() : dto.getPaymentDate());

        if (requestedPaymentMethod != null) {
            entity.setPaymentMethod(requestedPaymentMethod);
        }

        entity.setFee(financialCalculator.valueOrZero(dto.getFee()));
        entity.setLateInterest(financialCalculator.valueOrZero(dto.getLateInterest()));
        entity.setLateFee(financialCalculator.valueOrZero(dto.getLateFee()));
        entity.setDiscount(financialCalculator.valueOrZero(dto.getDiscount()));
        entity.setUpdatedBy(user);

        return toDTOWithLateCharges(repository.save(entity));
    }

    private void validatePayment(Payable entity, PayablePaymentDTO dto, BigDecimal paymentAmount) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException(PayableErrorMessages.PAID_PAYABLE_CANNOT_BE_PAID);
        }

        if (Boolean.TRUE.equals(entity.getCanceled())) {
            throw new IllegalArgumentException(PayableErrorMessages.CANCELED_PAYABLE_CANNOT_BE_PAID);
        }

        if (paymentAmount.compareTo(PayableConstants.ZERO) <= 0) {
            throw new IllegalArgumentException(PayableErrorMessages.PAYMENT_AMOUNT_MUST_BE_POSITIVE);
        }

        if (paymentAmount.compareTo(financialCalculator.getCurrentPaymentLimit(entity, dto)) > 0) {
            throw new IllegalArgumentException(PayableErrorMessages.PAYMENT_AMOUNT_EXCEEDS_PAYABLE);
        }
    }

    private void payTotal(
            Payable entity,
            PayablePaymentDTO dto,
            User user,
            PaymentMethod requestedPaymentMethod,
            BigDecimal paidAmount
    ) {
        entity.setPaid(true);
        entity.setPaymentDate(dto.getPaymentDate() == null ? today() : dto.getPaymentDate());

        if (requestedPaymentMethod != null) {
            entity.setPaymentMethod(requestedPaymentMethod);
        }

        entity.setSubtotal(paidAmount);
        entity.setFee(financialCalculator.valueOrZero(dto.getFee()));
        entity.setLateInterest(financialCalculator.valueOrZero(dto.getLateInterest()));
        entity.setLateFee(financialCalculator.valueOrZero(dto.getLateFee()));
        entity.setDiscount(financialCalculator.valueOrZero(dto.getDiscount()));
        entity.setRemainingBalance(PayableConstants.ZERO);
        entity.setPaidBy(user);
        entity.setUpdatedBy(user);
    }

    private User getAuthenticatedUser() {
        String username = authenticationFacade.getAuthenticatedUsername();
        if (username == null) {
            return null;
        }

        return userRepository.findByEmail(username);
    }

    private PayableDTO toDTOWithLateCharges(Payable entity) {
        PayableDTO dto = mapper.toDTO(entity);
        financialCalculator.fillLateCharges(entity, dto);
        return dto;
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
