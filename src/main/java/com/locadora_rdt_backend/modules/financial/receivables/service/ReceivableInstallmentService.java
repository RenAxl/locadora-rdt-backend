package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableErrorMessages;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants.DEFAULT_DESCRIPTION;
import static com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants.MONEY_SCALE;
import static com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants.ZERO;

@Service
public class ReceivableInstallmentService {

    private final ReceivableRepository repository;
    private final ReceivableMapper mapper;
    private final ReceivableFinancialCalculator financialCalculator;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;

    public ReceivableInstallmentService(ReceivableRepository repository, ReceivableMapper mapper,
                                        ReceivableFinancialCalculator financialCalculator,
                                        AuthenticationFacade authenticationFacade, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.financialCalculator = financialCalculator;
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<ReceivableDTO> installment(Receivable original, ReceivableInstallmentDTO dto) {
        if (Boolean.TRUE.equals(original.getPaid())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.PAID_RECEIVABLE_CANNOT_BE_INSTALLMENTED);
        }
        if (original.getParentReceivable() != null || repository.existsByParentReceivableId(original.getId())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.RECEIVABLE_ALREADY_INSTALLMENTED);
        }

        BigDecimal total = financialCalculator.valueOrZero(original.getAmount());
        BigDecimal base = total.divide(BigDecimal.valueOf(dto.getInstallments()), MONEY_SCALE, RoundingMode.DOWN);
        BigDecimal accumulated = ZERO;
        LocalDate firstDueDate = dto.getFirstDueDate() == null ? original.getDueDate() : dto.getFirstDueDate();
        User user = getAuthenticatedUser();
        List<Receivable> installments = new ArrayList<>();

        for (int i = 1; i <= dto.getInstallments(); i++) {
            BigDecimal value = i == dto.getInstallments() ? total.subtract(accumulated) : base;
            accumulated = accumulated.add(value);
            Receivable installment = copyBase(original);
            installment.setAmount(value);
            installment.setRemainingBalance(value);
            installment.setPaid(false);
            installment.setPaymentDate(null);
            installment.setDescription(buildDescription(original.getDescription(), i, dto.getInstallments()));
            installment.setDueDate(firstDueDate == null ? null : firstDueDate.plusMonths(i - 1L));
            installment.setParentReceivable(original);
            installment.setCreatedBy(user);
            installments.add(installment);
        }

        original.setCanceled(true);
        original.setUpdatedBy(user);
        repository.save(original);
        List<ReceivableDTO> result = new ArrayList<>();
        for (Receivable item : repository.saveAll(installments)) {
            ReceivableDTO resultItem = mapper.toDTO(item);
            financialCalculator.fillLateCharges(item, resultItem);
            result.add(resultItem);
        }
        return result;
    }

    private Receivable copyBase(Receivable source) {
        Receivable copy = new Receivable();
        copy.setDescription(source.getDescription());
        copy.setAmount(source.getAmount());
        copy.setDueDate(source.getDueDate());
        copy.setNote(source.getNote());
        copy.setFileName(source.getFileName());
        copy.setReference(source.getReference());
        copy.setReferenceId(source.getReferenceId());
        copy.setCustomer(source.getCustomer());
        copy.setPaymentMethod(source.getPaymentMethod());
        copy.setPaymentFrequency(source.getPaymentFrequency());
        copy.setResidual(false);
        copy.setCanceled(false);
        return copy;
    }

    private String buildDescription(String description, int installment, int total) {
        String base = description == null || description.trim().isEmpty() ? DEFAULT_DESCRIPTION : description.trim();
        return base + " (" + installment + "/" + total + ")";
    }

    private User getAuthenticatedUser() {
        String username = authenticationFacade.getAuthenticatedUsername();
        return username == null ? null : userRepository.findByEmail(username);
    }
}
