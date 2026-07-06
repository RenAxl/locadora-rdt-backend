package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.payables.constants.PayableErrorMessages;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.suppliers.repository.SupplierRepository;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.repository.PaymentFrequencyRepository;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment.methods.repository.PaymentMethodRepository;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInsertDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableReportDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableSaveDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payables.mapper.PayableMapper;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payables.model.PayableStatus;
import com.locadora_rdt_backend.modules.financial.payables.repository.PayableRepository;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PayableServiceImpl implements PayableService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal FILTER_AMOUNT_DISABLED = BigDecimal.valueOf(-1);
    private static final LocalDate FILTER_DATE_DISABLED = LocalDate.of(1970, 1, 1);
    private static final long FILTER_ID_DISABLED = -1L;

    private final PayableRepository repository;
    private final PayableMapper mapper;
    private final SupplierRepository supplierRepository;
    private final EmployeeRepository employeeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentFrequencyRepository paymentFrequencyRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final PayableFilterNormalizer filterNormalizer;
    private final PayableFinancialCalculator financialCalculator;
    private final Clock clock;

    public PayableServiceImpl(
            PayableRepository repository,
            PayableMapper mapper,
            SupplierRepository supplierRepository,
            EmployeeRepository employeeRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentFrequencyRepository paymentFrequencyRepository,
            UserRepository userRepository,
            AuthenticationFacade authenticationFacade,
            PayableFilterNormalizer filterNormalizer,
            PayableFinancialCalculator financialCalculator,
            Clock clock
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.supplierRepository = supplierRepository;
        this.employeeRepository = employeeRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentFrequencyRepository = paymentFrequencyRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.filterNormalizer = filterNormalizer;
        this.financialCalculator = financialCalculator;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayableDTO> findAllPaged(String description, PageRequest pageRequest) {
        PayableFilterDTO filters = new PayableFilterDTO();
        filters.setSearch(description);
        filters.setStatus("ALL");
        filters.setPeriodType("DUE_DATE");
        filters.setOrderBy("dueDate");
        filters.setDirection("ASC");
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
        applyRelations(entity, dto);
        entity.setCreatedBy(getAuthenticatedUser());

        if (entity.getPaid()) {
            entity.setPaidBy(entity.getCreatedBy());
            entity.setSubtotal(financialCalculator.valueOrZero(entity.getAmount()));
            entity.setRemainingBalance(ZERO);
        }

        entity = repository.save(entity);
        return toDTOWithLateCharges(entity);
    }

    @Override
    @Transactional
    public PayableDTO update(Long id, PayableUpdateDTO dto) {
        Payable entity = findEntity(id);
        boolean wasPartiallyPaid = isPartiallyPaid(entity);
        BigDecimal previousRemainingBalance = entity.getRemainingBalance();
        LocalDate previousPaymentDate = entity.getPaymentDate();

        mapper.updateEntity(entity, dto);
        applyRelations(entity, dto);
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
        Payable entity = findEntity(id);
        BigDecimal paymentAmount = financialCalculator.valueOrZero(dto.getPaymentAmount());
        validatePayment(entity, dto, paymentAmount);

        User user = getAuthenticatedUser();
        BigDecimal amount = financialCalculator.valueOrZero(entity.getAmount());
        BigDecimal openAmount = financialCalculator.getOpenAmount(entity);
        BigDecimal paidAmount = amount.subtract(openAmount);
        PaymentMethod requestedPaymentMethod = findPaymentMethod(normalizeId(dto.getPaymentMethodId()));

        BigDecimal paymentLimit = financialCalculator.getCurrentPaymentLimit(entity, dto);

        if (paymentAmount.compareTo(paymentLimit) == 0 || paymentAmount.compareTo(openAmount) >= 0) {
            payTotal(entity, dto, user, requestedPaymentMethod, paidAmount.add(openAmount));
            return toDTOWithLateCharges(repository.save(entity));
        }

        BigDecimal remaining = openAmount.subtract(paymentAmount);
        entity.setSubtotal(paidAmount.add(paymentAmount));
        entity.setRemainingBalance(remaining);
        entity.setPaid(false);
        if (dto.getPaymentDate() == null) {
            entity.setPaymentDate(today());
        } else {
            entity.setPaymentDate(dto.getPaymentDate());
        }

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

    @Override
    @Transactional
    public List<PayableDTO> installment(Long id, PayableInstallmentDTO dto) {
        Payable original = findEntity(id);

        if (Boolean.TRUE.equals(original.getPaid())) {
            throw new IllegalArgumentException("Não é possível parcelar conta já paga.");
        }

        validateNotInstallmented(original);

        BigDecimal total = financialCalculator.valueOrZero(original.getAmount());
        BigDecimal base = total.divide(BigDecimal.valueOf(dto.getInstallments()), 2, RoundingMode.DOWN);
        BigDecimal accumulated = ZERO;
        List<Payable> installments = new ArrayList<>();
        LocalDate firstDueDate;

        if (dto.getFirstDueDate() == null) {
            firstDueDate = original.getDueDate();
        } else {
            firstDueDate = dto.getFirstDueDate();
        }

        for (int i = 1; i <= dto.getInstallments(); i++) {
            BigDecimal value;

            if (i == dto.getInstallments()) {
                value = total.subtract(accumulated);
            } else {
                value = base;
            }

            accumulated = accumulated.add(value);

            Payable installment = copyBase(original);
            installment.setId(null);
            installment.setAmount(value);
            installment.setRemainingBalance(value);
            installment.setPaid(false);
            installment.setPaymentDate(null);
            installment.setDescription(buildInstallmentDescription(original.getDescription(), i, dto.getInstallments()));

            if (firstDueDate == null) {
                installment.setDueDate(null);
            } else {
                installment.setDueDate(firstDueDate.plusMonths(i - 1L));
            }

            installment.setParentPayable(original);
            installment.setCreatedBy(getAuthenticatedUser());
            installments.add(installment);
        }

        original.setCanceled(true);
        original.setUpdatedBy(getAuthenticatedUser());
        repository.save(original);

        List<Payable> savedInstallments = repository.saveAll(installments);
        List<PayableDTO> result = new ArrayList<>();

        for (Payable item : savedInstallments) {
            PayableDTO dtoResult = toDTOWithLateCharges(item);
            result.add(dtoResult);
        }

        return result;
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
        PayableFilterDTO reportFilters = new PayableFilterDTO();
        reportFilters.setSearch(description);
        reportFilters.setStartDate(startDate);
        reportFilters.setEndDate(endDate);
        reportFilters.setStatus(status);
        reportFilters.setPeriodType(dateType);

        List<Payable> items = repository.findAll(filters(filterNormalizer.normalize(reportFilters)));
        BigDecimal total = sum(items);
        List<Payable> paidItems = new ArrayList<>();

        for (Payable item : items) {
            if (Boolean.TRUE.equals(item.getPaid())) {
                paidItems.add(item);
            }
        }

        BigDecimal paid = sum(paidItems);
        BigDecimal open = total.subtract(paid);

        return new PayableReportDTO((long) items.size(), total, paid, open);
    }

    private Specification<Payable> filters(PayableFilterDTO filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getSearch() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + filters.getSearch().toLowerCase() + "%"));
            }

            if (PayableStatus.PAID.name().equals(filters.getStatus())) {
                predicates.add(cb.isTrue(root.get("paid")));
                predicates.add(cb.isFalse(root.get("canceled")));
            } else if (PayableStatus.PENDING.name().equals(filters.getStatus())) {
                predicates.add(cb.isFalse(root.get("paid")));
                predicates.add(cb.isFalse(root.get("canceled")));
                predicates.add(cb.or(cb.isNull(root.get("dueDate")), cb.greaterThanOrEqualTo(root.get("dueDate"), today())));
            } else if (PayableStatus.OVERDUE.name().equals(filters.getStatus())) {
                predicates.add(cb.isFalse(root.get("paid")));
                predicates.add(cb.isFalse(root.get("canceled")));
                predicates.add(cb.lessThan(root.get("dueDate"), today()));
            } else if (PayableStatus.PARTIALLY_PAID.name().equals(filters.getStatus())) {
                predicates.add(cb.isFalse(root.get("paid")));
                predicates.add(cb.isFalse(root.get("canceled")));
                predicates.add(cb.greaterThan(root.get("remainingBalance"), ZERO));
                predicates.add(cb.lessThan(root.get("remainingBalance"), root.get("amount")));
            } else if (PayableStatus.CANCELED.name().equals(filters.getStatus())) {
                predicates.add(cb.isTrue(root.get("canceled")));
            }

            if (filters.getStartDate() != null || filters.getEndDate() != null) {
                if ("CREATED_DATE".equals(filters.getPeriodType())) {
                    Path<java.time.Instant> path = root.get("createdAt");

                    if (filters.getStartDate() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(path, filters.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC)));
                    }

                    if (filters.getEndDate() != null) {
                        predicates.add(cb.lessThan(path, filters.getEndDate().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
                    }
                } else {
                    Path<LocalDate> path = datePath(root, filters.getPeriodType());

                    if (filters.getStartDate() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(path, filters.getStartDate()));
                    }

                    if (filters.getEndDate() != null) {
                        predicates.add(cb.lessThanOrEqualTo(path, filters.getEndDate()));
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Path<LocalDate> datePath(javax.persistence.criteria.Root<Payable> root, String dateType) {
        if ("PAYMENT_DATE".equals(dateType)) {
            return root.get("paymentDate");
        }

        return root.get("dueDate");
    }

    private void applyRelations(Payable entity, PayableSaveDTO dto) {
        entity.setSupplier(findSupplier(normalizeId(dto.getSupplierId())));
        entity.setEmployee(findEmployee(normalizeId(dto.getEmployeeId())));
        entity.setPaymentMethod(findPaymentMethod(normalizeId(dto.getPaymentMethodId())));
        entity.setPaymentFrequency(findPaymentFrequency(normalizeId(dto.getPaymentFrequencyId())));
    }

    private void validatePayment(Payable entity, PayablePaymentDTO dto, BigDecimal paymentAmount) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException("Conta já está paga.");
        }

        if (paymentAmount.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de baixa deve ser maior que zero.");
        }

        if (paymentAmount.compareTo(financialCalculator.getCurrentPaymentLimit(entity, dto)) > 0) {
            throw new IllegalArgumentException("Valor de baixa não pode ser maior que o valor da conta.");
        }
    }

    private void validateNotInstallmented(Payable entity) {
        if (entity.getParentPayable() != null || repository.existsByParentPayableId(entity.getId())) {
            throw new IllegalArgumentException(PayableErrorMessages.PAYABLE_ALREADY_INSTALLMENTED);
        }
    }

    private boolean isPartiallyPaid(Payable entity) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            return false;
        }

        BigDecimal amount = financialCalculator.valueOrZero(entity.getAmount());
        BigDecimal remainingBalance = entity.getRemainingBalance();

        if (remainingBalance == null) {
            return false;
        }

        if (remainingBalance.compareTo(ZERO) <= 0) {
            return false;
        }

        return remainingBalance.compareTo(amount) < 0;
    }

    private void payTotal(Payable entity, PayablePaymentDTO dto, User user, PaymentMethod requestedPaymentMethod, BigDecimal paidAmount) {
        entity.setPaid(true);

        if (dto.getPaymentDate() == null) {
            entity.setPaymentDate(today());
        } else {
            entity.setPaymentDate(dto.getPaymentDate());
        }

        if (requestedPaymentMethod != null) {
            entity.setPaymentMethod(requestedPaymentMethod);
        }

        entity.setSubtotal(paidAmount);
        entity.setFee(financialCalculator.valueOrZero(dto.getFee()));
        entity.setLateInterest(financialCalculator.valueOrZero(dto.getLateInterest()));
        entity.setLateFee(financialCalculator.valueOrZero(dto.getLateFee()));
        entity.setDiscount(financialCalculator.valueOrZero(dto.getDiscount()));
        entity.setRemainingBalance(ZERO);
        entity.setPaidBy(user);
        entity.setUpdatedBy(user);
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
            base = "Conta a pagar";
        } else {
            base = description.trim();
        }

        return base + " (" + installment + "/" + total + ")";
    }

    private BigDecimal sum(List<Payable> items) {
        BigDecimal total = ZERO;

        for (Payable item : items) {
            total = total.add(financialCalculator.valueOrZero(item.getAmount()));
        }

        return total;
    }

    private PayableDTO toDTOWithLateCharges(Payable entity) {
        PayableDTO dto = mapper.toDTO(entity);
        financialCalculator.fillLateCharges(entity, dto);
        return dto;
    }

    private Long normalizeId(Long id) {
        if (id == null) {
            return null;
        }

        if (id <= 0) {
            return null;
        }

        return id;
    }

    private Long idFilterOrDisabled(Long id) {
        if (id == null) {
            return FILTER_ID_DISABLED;
        }

        return id;
    }

    private BigDecimal amountFilterOrDisabled(BigDecimal amount) {
        if (amount == null) {
            return FILTER_AMOUNT_DISABLED;
        }

        return amount;
    }

    private LocalDate dateFilterOrDisabled(LocalDate date) {
        if (date == null) {
            return FILTER_DATE_DISABLED;
        }

        return date;
    }

    private Supplier findSupplier(Long id) {
        if (id == null) {
            return null;
        }

        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);

        if (optionalSupplier.isEmpty()) {
            throw new ResourceNotFoundException("Fornecedor não encontrado. Id: " + id);
        }

        return optionalSupplier.get();
    }

    private Employee findEmployee(Long id) {
        if (id == null) {
            return null;
        }

        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (optionalEmployee.isEmpty()) {
            throw new ResourceNotFoundException("Funcionário não encontrado. Id: " + id);
        }

        return optionalEmployee.get();
    }

    private PaymentMethod findPaymentMethod(Long id) {
        if (id == null) {
            return null;
        }

        Optional<PaymentMethod> optionalPaymentMethod = paymentMethodRepository.findById(id);

        if (optionalPaymentMethod.isEmpty()) {
            throw new ResourceNotFoundException("Forma de pagamento não encontrada. Id: " + id);
        }

        return optionalPaymentMethod.get();
    }

    private PaymentFrequency findPaymentFrequency(Long id) {
        if (id == null) {
            return null;
        }

        Optional<PaymentFrequency> optionalPaymentFrequency = paymentFrequencyRepository.findById(id);

        if (optionalPaymentFrequency.isEmpty()) {
            throw new ResourceNotFoundException("Frequência não encontrada. Id: " + id);
        }

        return optionalPaymentFrequency.get();
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
            throw new ResourceNotFoundException(PayableErrorMessages.PAYABLE_NOT_FOUND + " Id: " + id);
        }

        return optionalPayable.get();
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
