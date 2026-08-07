package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payables.constants.PayableConstants;
import com.locadora_rdt_backend.modules.financial.payables.constants.PayableErrorMessages;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInsertDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableReportDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payables.mapper.PayableMapper;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PayableServiceImpl implements PayableService {

    private final PayableRepository repository;
    private final PayableMapper mapper;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final PayableFilterNormalizer filterNormalizer;
    private final PayableFinancialCalculator financialCalculator;
    private final PayableRelationService relationService;
    private final PayablePaymentService paymentService;
    private final PayableInstallmentService installmentService;
    private final PayableReportService reportService;

    public PayableServiceImpl(
            PayableRepository repository,
            PayableMapper mapper,
            UserRepository userRepository,
            AuthenticationFacade authenticationFacade,
            PayableFilterNormalizer filterNormalizer,
            PayableFinancialCalculator financialCalculator,
            PayableRelationService relationService,
            PayablePaymentService paymentService,
            PayableInstallmentService installmentService,
            PayableReportService reportService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.filterNormalizer = filterNormalizer;
        this.financialCalculator = financialCalculator;
        this.relationService = relationService;
        this.paymentService = paymentService;
        this.installmentService = installmentService;
        this.reportService = reportService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayableDTO> findAllPaged(String description, PageRequest pageRequest) {
        PayableFilterDTO filters = new PayableFilterDTO();
        filters.setSearch(description);
        filters.setStatus(PayableConstants.STATUS_ALL);
        filters.setPeriodType(PayableConstants.PERIOD_DUE_DATE);
        filters.setOrderBy(PayableConstants.ORDER_BY_DUE_DATE);
        filters.setDirection(PayableConstants.DIRECTION_ASC);
        return findAllPaged(filters, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayableDTO> findAllPaged(PayableFilterDTO filters, PageRequest pageRequest) {
        PayableFilterDTO normalized = filterNormalizer.normalize(filters);

        return repository.findWithFilters(
                        normalized.getSearch(),
                        dateFilterOrDisabled(normalized.getStartDate()),
                        dateFilterOrDisabled(normalized.getEndDate()),
                        normalized.getStartDate() != null,
                        normalized.getEndDate() != null,
                        normalized.getStatus(),
                        normalized.getPeriodType(),
                        idFilterOrDisabled(normalized.getSupplierId()),
                        idFilterOrDisabled(normalized.getEmployeeId()),
                        idFilterOrDisabled(normalized.getPaymentMethodId()),
                        idFilterOrDisabled(normalized.getPaymentFrequencyId()),
                        amountFilterOrDisabled(normalized.getMinimumAmount()),
                        amountFilterOrDisabled(normalized.getMaximumAmount()),
                        normalized.getOrderBy(),
                        normalized.getDirection(),
                        pageRequest
                )
                .map(this::toDTOWithLateCharges);
    }

    @Override
    @Transactional(readOnly = true)
    public PayableDetailsDTO findById(Long id) {
        Payable entity = findEntity(id);
        PayableDetailsDTO dto = mapper.toDetailsDTO(entity);
        financialCalculator.fillLateCharges(entity, dto);
        return dto;
    }

    @Override
    @Transactional
    public PayableDTO insert(PayableInsertDTO dto) {
        Payable entity = mapper.toEntity(dto);
        relationService.applyRelations(entity, dto);
        entity.setCreatedBy(getAuthenticatedUser());

        if (entity.getPaid()) {
            entity.setPaidBy(entity.getCreatedBy());
            entity.setSubtotal(financialCalculator.valueOrZero(entity.getAmount()));
            entity.setRemainingBalance(PayableConstants.ZERO);
        }

        entity = repository.save(entity);
        return toDTOWithLateCharges(entity);
    }

    @Override
    @Transactional
    public PayableDTO update(Long id, PayableUpdateDTO dto) {
        Payable entity = findEntity(id);

        if (Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException(PayableErrorMessages.PAID_PAYABLE_CANNOT_BE_UPDATED);
        }

        if (Boolean.TRUE.equals(entity.getCanceled())) {
            throw new IllegalArgumentException(PayableErrorMessages.CANCELED_PAYABLE_CANNOT_BE_UPDATED);
        }

        boolean wasPartiallyPaid = isPartiallyPaid(entity);
        BigDecimal previousRemainingBalance = entity.getRemainingBalance();
        LocalDate previousPaymentDate = entity.getPaymentDate();

        mapper.updateEntity(entity, dto);
        relationService.applyRelations(entity, dto);
        entity.setUpdatedBy(getAuthenticatedUser());

        if (wasPartiallyPaid) {
            entity.setPaid(false);
            entity.setRemainingBalance(previousRemainingBalance);

            if (dto.getPaymentDate() == null) {
                entity.setPaymentDate(previousPaymentDate);
            }
        }

        if (entity.getPaid()) {
            if (entity.getPaidBy() == null) {
                entity.setPaidBy(entity.getUpdatedBy());
            }

            entity.setRemainingBalance(PayableConstants.ZERO);
        }

        return toDTOWithLateCharges(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Payable entity = findEntity(id);

        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(PayableErrorMessages.DATABASE_INTEGRITY_VIOLATION);
        }
    }

    @Override
    @Transactional
    public PayableDTO pay(Long id, PayablePaymentDTO dto) {
        return paymentService.pay(findEntity(id), dto);
    }

    @Override
    @Transactional
    public List<PayableDTO> installment(Long id, PayableInstallmentDTO dto) {
        return installmentService.installment(findEntity(id), dto);
    }

    @Override
    @Transactional(readOnly = true)
    public PayableReportDTO report(
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String dateType
    ) {
        return reportService.report(description, startDate, endDate, status, dateType);
    }

    private boolean isPartiallyPaid(Payable entity) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            return false;
        }

        BigDecimal amount = financialCalculator.valueOrZero(entity.getAmount());
        BigDecimal remainingBalance = entity.getRemainingBalance();

        if (remainingBalance == null || remainingBalance.compareTo(PayableConstants.ZERO) <= 0) {
            return false;
        }

        return remainingBalance.compareTo(amount) < 0;
    }

    private PayableDTO toDTOWithLateCharges(Payable entity) {
        PayableDTO dto = mapper.toDTO(entity);
        financialCalculator.fillLateCharges(entity, dto);
        return dto;
    }

    private Long idFilterOrDisabled(Long id) {
        return id == null ? PayableConstants.FILTER_ID_DISABLED : id;
    }

    private BigDecimal amountFilterOrDisabled(BigDecimal amount) {
        return amount == null ? PayableConstants.FILTER_AMOUNT_DISABLED : amount;
    }

    private LocalDate dateFilterOrDisabled(LocalDate date) {
        return date == null ? PayableConstants.FILTER_DATE_DISABLED : date;
    }

    private User getAuthenticatedUser() {
        String username = authenticationFacade.getAuthenticatedUsername();
        if (username == null) {
            return null;
        }

        return userRepository.findByEmail(username);
    }

    private Payable findEntity(Long id) {
        Optional<Payable> optionalPayable = repository.findById(id);

        if (optionalPayable.isEmpty()) {
            throw new ResourceNotFoundException(
                    PayableErrorMessages.PAYABLE_NOT_FOUND + PayableErrorMessages.ID_COMPLEMENT + id
            );
        }

        return optionalPayable.get();
    }
}
