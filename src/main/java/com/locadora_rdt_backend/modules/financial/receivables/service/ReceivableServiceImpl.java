package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableErrorMessages;
import com.locadora_rdt_backend.modules.financial.receivables.dto.*;
import com.locadora_rdt_backend.modules.financial.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
import com.locadora_rdt_backend.modules.identity.users.model.User;
import com.locadora_rdt_backend.modules.identity.users.repository.UserRepository;
import com.locadora_rdt_backend.modules.rentals.rental.model.Rental;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants.*;

@Service
public class ReceivableServiceImpl implements ReceivableService {

    private final ReceivableRepository repository;
    private final ReceivableMapper mapper;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final ReceivableFilterNormalizer filterNormalizer;
    private final ReceivableFinancialCalculator financialCalculator;
    private final ReceivableRelationService relationService;
    private final ReceivablePaymentService paymentService;
    private final ReceivableInstallmentService installmentService;
    private final ReceivableReportService reportService;
    private final ReceivableDocumentService documentService;
    private final ReceivableRentalService rentalService;

    public ReceivableServiceImpl(ReceivableRepository repository, ReceivableMapper mapper,
                                 UserRepository userRepository, AuthenticationFacade authenticationFacade,
                                 ReceivableFilterNormalizer filterNormalizer,
                                 ReceivableFinancialCalculator financialCalculator,
                                 ReceivableRelationService relationService, ReceivablePaymentService paymentService,
                                 ReceivableInstallmentService installmentService,
                                 ReceivableReportService reportService, ReceivableDocumentService documentService,
                                 ReceivableRentalService rentalService) {
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
        this.documentService = documentService;
        this.rentalService = rentalService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceivableDTO> findAllPaged(String description, PageRequest pageRequest) {
        ReceivableFilterDTO filters = new ReceivableFilterDTO();
        filters.setSearch(description);
        filters.setStatus(STATUS_ALL);
        filters.setPeriodType(PERIOD_DUE_DATE);
        filters.setOrderBy(ORDER_BY_DUE_DATE);
        filters.setDirection(DIRECTION_ASC);
        return findAllPaged(filters, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceivableDTO> findAllPaged(ReceivableFilterDTO filters, PageRequest pageRequest) {
        ReceivableFilterDTO normalized = filterNormalizer.normalize(filters);
        return repository.findWithFilters(normalized.getSearch(), date(normalized.getStartDate()),
                        date(normalized.getEndDate()), normalized.getStartDate() != null,
                        normalized.getEndDate() != null, normalized.getStatus(), normalized.getPeriodType(),
                        id(normalized.getCustomerId()), id(normalized.getPaymentMethodId()),
                        id(normalized.getPaymentFrequencyId()), amount(normalized.getMinimumAmount()),
                        amount(normalized.getMaximumAmount()), normalized.getOrderBy(), normalized.getDirection(),
                        pageRequest)
                .map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceivableDetailsDTO findById(Long id) {
        Receivable entity = findEntity(id);
        ReceivableDetailsDTO dto = mapper.toDetailsDTO(entity);
        financialCalculator.fillLateCharges(entity, dto);
        return dto;
    }

    @Override
    @Transactional
    public ReceivableDTO insert(ReceivableInsertDTO dto) {
        validateCustomerOrDescription(dto);
        Receivable entity = mapper.toEntity(dto);
        relationService.applyRelations(entity, dto);
        entity.setCreatedBy(getAuthenticatedUser());
        if (entity.getPaid()) {
            entity.setPaidBy(entity.getCreatedBy());
            entity.setSubtotal(financialCalculator.valueOrZero(entity.getAmount()));
            entity.setRemainingBalance(ZERO);
        }
        return toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void createFromRental(Rental rental) {
        rentalService.createFromRental(rental);
    }

    @Override
    @Transactional
    public ReceivableDTO update(Long id, ReceivableUpdateDTO dto) {
        Receivable entity = findEntity(id);
        if (Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.PAID_RECEIVABLE_CANNOT_BE_UPDATED);
        }
        if (Boolean.TRUE.equals(entity.getCanceled())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.CANCELED_RECEIVABLE_CANNOT_BE_UPDATED);
        }
        validateCustomerOrDescription(dto);
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
            entity.setRemainingBalance(ZERO);
        }
        return toDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Receivable entity = findEntity(id);
        try {
            repository.delete(entity);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(ReceivableErrorMessages.DATABASE_INTEGRITY_VIOLATION);
        }
    }

    @Override
    @Transactional
    public ReceivableDTO pay(Long id, ReceivablePaymentDTO dto) {
        return paymentService.pay(findEntity(id), dto);
    }

    @Override
    @Transactional
    public List<ReceivableDTO> installment(Long id, ReceivableInstallmentDTO dto) {
        return installmentService.installment(findEntity(id), dto);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceivableReportDTO report(String description, LocalDate startDate, LocalDate endDate,
                                      String status, String dateType) {
        return reportService.report(description, startDate, endDate, status, dateType);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] receipt(Long id) {
        return documentService.receipt(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] fiscalCoupon(Long id) {
        return documentService.fiscalCoupon(findEntity(id));
    }

    private void validateCustomerOrDescription(ReceivableSaveDTO dto) {
        boolean hasCustomer = relationService.normalizeId(dto.getCustomerId()) != null;
        boolean hasDescription = dto.getDescription() != null && !dto.getDescription().trim().isEmpty();
        if (!hasCustomer && !hasDescription) {
            throw new IllegalArgumentException(ReceivableErrorMessages.CUSTOMER_OR_DESCRIPTION_REQUIRED);
        }
    }

    private boolean isPartiallyPaid(Receivable entity) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            return false;
        }
        BigDecimal remaining = entity.getRemainingBalance();
        return remaining != null && remaining.compareTo(ZERO) > 0
                && remaining.compareTo(financialCalculator.valueOrZero(entity.getAmount())) < 0;
    }

    private ReceivableDTO toDTO(Receivable entity) {
        ReceivableDTO dto = mapper.toDTO(entity);
        financialCalculator.fillLateCharges(entity, dto);
        return dto;
    }

    private User getAuthenticatedUser() {
        String username = authenticationFacade.getAuthenticatedUsername();
        return username == null ? null : userRepository.findByEmail(username);
    }

    private Receivable findEntity(Long id) {
        Optional<Receivable> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException(
                    ReceivableErrorMessages.RECEIVABLE_NOT_FOUND + ReceivableErrorMessages.ID_COMPLEMENT + id
            );
        }
        return entity.get();
    }

    private Long id(Long value) { return value == null ? FILTER_ID_DISABLED : value; }
    private BigDecimal amount(BigDecimal value) { return value == null ? FILTER_AMOUNT_DISABLED : value; }
    private LocalDate date(LocalDate value) { return value == null ? FILTER_DATE_DISABLED : value; }
}
