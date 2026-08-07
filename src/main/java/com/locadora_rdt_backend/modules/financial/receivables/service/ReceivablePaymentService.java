package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payment_methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableErrorMessages;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

import static com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants.ZERO;

@Service
public class ReceivablePaymentService {

    private final ReceivableRepository repository;
    private final ReceivableMapper mapper;
    private final ReceivableRelationService relationService;
    private final ReceivableFinancialCalculator financialCalculator;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;
    private final Clock clock;

    public ReceivablePaymentService(
            ReceivableRepository repository,
            ReceivableMapper mapper,
            ReceivableRelationService relationService,
            ReceivableFinancialCalculator financialCalculator,
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
    public ReceivableDTO pay(Receivable entity, ReceivablePaymentDTO dto) {
        BigDecimal paymentAmount = financialCalculator.valueOrZero(dto.getPaymentAmount());
        validatePayment(entity, dto, paymentAmount);
        User user = getAuthenticatedUser();
        BigDecimal amount = financialCalculator.valueOrZero(entity.getAmount());
        BigDecimal openAmount = financialCalculator.getOpenAmount(entity);
        BigDecimal paidAmount = amount.subtract(openAmount);
        PaymentMethod paymentMethod = relationService.findPaymentMethod(dto.getPaymentMethodId());
        BigDecimal paymentLimit = financialCalculator.getCurrentPaymentLimit(entity, dto);

        if (paymentAmount.compareTo(paymentLimit) == 0 || paymentAmount.compareTo(openAmount) >= 0) {
            payTotal(entity, dto, user, paymentMethod, paidAmount.add(openAmount));
            return toDTO(repository.save(entity));
        }

        entity.setSubtotal(paidAmount.add(paymentAmount));
        entity.setRemainingBalance(openAmount.subtract(paymentAmount));
        entity.setPaid(false);
        entity.setPaymentDate(dto.getPaymentDate() == null ? today() : dto.getPaymentDate());
        if (paymentMethod != null) {
            entity.setPaymentMethod(paymentMethod);
        }
        applyPaymentValues(entity, dto);
        entity.setUpdatedBy(user);
        return toDTO(repository.save(entity));
    }

    private void validatePayment(Receivable entity, ReceivablePaymentDTO dto, BigDecimal paymentAmount) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.PAID_RECEIVABLE_CANNOT_BE_PAID);
        }
        if (Boolean.TRUE.equals(entity.getCanceled())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.CANCELED_RECEIVABLE_CANNOT_BE_PAID);
        }
        if (paymentAmount.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException(ReceivableErrorMessages.PAYMENT_AMOUNT_MUST_BE_POSITIVE);
        }
        if (paymentAmount.compareTo(financialCalculator.getCurrentPaymentLimit(entity, dto)) > 0) {
            throw new IllegalArgumentException(ReceivableErrorMessages.PAYMENT_AMOUNT_EXCEEDS_RECEIVABLE);
        }
    }

    private void payTotal(Receivable entity, ReceivablePaymentDTO dto, User user,
                          PaymentMethod paymentMethod, BigDecimal paidAmount) {
        entity.setPaid(true);
        entity.setPaymentDate(dto.getPaymentDate() == null ? today() : dto.getPaymentDate());
        if (paymentMethod != null) {
            entity.setPaymentMethod(paymentMethod);
        }
        entity.setSubtotal(paidAmount);
        applyPaymentValues(entity, dto);
        entity.setRemainingBalance(ZERO);
        entity.setPaidBy(user);
        entity.setUpdatedBy(user);
    }

    private void applyPaymentValues(Receivable entity, ReceivablePaymentDTO dto) {
        entity.setFee(financialCalculator.valueOrZero(dto.getFee()));
        entity.setLateInterest(financialCalculator.valueOrZero(dto.getLateInterest()));
        entity.setLateFee(financialCalculator.valueOrZero(dto.getLateFee()));
        entity.setDiscount(financialCalculator.valueOrZero(dto.getDiscount()));
    }

    private User getAuthenticatedUser() {
        String username = authenticationFacade.getAuthenticatedUsername();
        return username == null ? null : userRepository.findByEmail(username);
    }

    private ReceivableDTO toDTO(Receivable entity) {
        ReceivableDTO dto = mapper.toDTO(entity);
        financialCalculator.fillLateCharges(entity, dto);
        return dto;
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
