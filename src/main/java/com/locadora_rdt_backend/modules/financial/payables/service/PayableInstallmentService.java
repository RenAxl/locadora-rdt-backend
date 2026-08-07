package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payables.constants.PayableConstants;
import com.locadora_rdt_backend.modules.financial.payables.constants.PayableErrorMessages;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.payables.mapper.PayableMapper;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayableInstallmentService {

    private final PayableRepository repository;
    private final PayableMapper mapper;
    private final PayableFinancialCalculator financialCalculator;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;

    public PayableInstallmentService(
            PayableRepository repository,
            PayableMapper mapper,
            PayableFinancialCalculator financialCalculator,
            AuthenticationFacade authenticationFacade,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.financialCalculator = financialCalculator;
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<PayableDTO> installment(Payable original, PayableInstallmentDTO dto) {
        if (Boolean.TRUE.equals(original.getPaid())) {
            throw new IllegalArgumentException(PayableErrorMessages.PAID_PAYABLE_CANNOT_BE_INSTALLMENTED);
        }

        validateNotInstallmented(original);

        BigDecimal total = financialCalculator.valueOrZero(original.getAmount());
        BigDecimal base = total.divide(
                BigDecimal.valueOf(dto.getInstallments()),
                PayableConstants.MONEY_SCALE,
                RoundingMode.DOWN
        );
        BigDecimal accumulated = PayableConstants.ZERO;
        List<Payable> installments = new ArrayList<>();
        LocalDate firstDueDate = dto.getFirstDueDate() == null ? original.getDueDate() : dto.getFirstDueDate();
        User authenticatedUser = getAuthenticatedUser();

        for (int i = 1; i <= dto.getInstallments(); i++) {
            BigDecimal value = i == dto.getInstallments() ? total.subtract(accumulated) : base;
            accumulated = accumulated.add(value);

            Payable installment = copyBase(original);
            installment.setId(null);
            installment.setAmount(value);
            installment.setRemainingBalance(value);
            installment.setPaid(false);
            installment.setPaymentDate(null);
            installment.setDescription(buildInstallmentDescription(
                    original.getDescription(),
                    i,
                    dto.getInstallments()
            ));
            installment.setDueDate(firstDueDate == null ? null : firstDueDate.plusMonths(i - 1L));
            installment.setParentPayable(original);
            installment.setCreatedBy(authenticatedUser);
            installments.add(installment);
        }

        original.setCanceled(true);
        original.setUpdatedBy(authenticatedUser);
        repository.save(original);

        List<Payable> savedInstallments = repository.saveAll(installments);
        List<PayableDTO> result = new ArrayList<>();

        for (Payable item : savedInstallments) {
            result.add(toDTOWithLateCharges(item));
        }

        return result;
    }

    private void validateNotInstallmented(Payable entity) {
        if (entity.getParentPayable() != null || repository.existsByParentPayableId(entity.getId())) {
            throw new IllegalArgumentException(PayableErrorMessages.PAYABLE_ALREADY_INSTALLMENTED);
        }
    }

    private Payable copyBase(Payable source) {
        Payable copy = new Payable();
        copy.setDescription(source.getDescription());
        copy.setAmount(source.getAmount());
        copy.setDueDate(source.getDueDate());
        copy.setNote(source.getNote());
        copy.setFileName(source.getFileName());
        copy.setReference(source.getReference());
        copy.setReferenceId(source.getReferenceId());
        copy.setSupplier(source.getSupplier());
        copy.setEmployee(source.getEmployee());
        copy.setPaymentMethod(source.getPaymentMethod());
        copy.setPaymentFrequency(source.getPaymentFrequency());
        copy.setResidual(false);
        copy.setCanceled(false);
        return copy;
    }

    private String buildInstallmentDescription(String description, int installment, int total) {
        String base;

        if (description == null || description.trim().isEmpty()) {
            base = PayableConstants.DEFAULT_DESCRIPTION;
        } else {
            base = description.trim();
        }

        return base + " (" + installment + "/" + total + ")";
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
}
