package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableErrorMessages;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.repository.PaymentFrequencyRepository;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment.methods.repository.PaymentMethodRepository;
import com.locadora_rdt_backend.modules.financial.payment.settings.model.FinancialSetting;
import com.locadora_rdt_backend.modules.financial.payment.settings.repository.FinancialSettingRepository;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInsertDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableInstallmentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivablePaymentDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableReportDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableSaveDTO;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableUpdateDTO;
import com.locadora_rdt_backend.modules.financial.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.financial.receivables.repository.ReceivableRepository;
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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceivableServiceImpl implements ReceivableService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal FILTER_AMOUNT_DISABLED = BigDecimal.valueOf(-1);
    private static final LocalDate FILTER_DATE_DISABLED = LocalDate.of(1970, 1, 1);
    private static final long FILTER_ID_DISABLED = -1L;

    private final ReceivableRepository repository;
    private final ReceivableMapper mapper;
    private final CustomerRepository customerRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentFrequencyRepository paymentFrequencyRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final FinancialSettingRepository financialSettingRepository;

    public ReceivableServiceImpl(
            ReceivableRepository repository,
            ReceivableMapper mapper,
            CustomerRepository customerRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentFrequencyRepository paymentFrequencyRepository,
            UserRepository userRepository,
            AuthenticationFacade authenticationFacade,
            FinancialSettingRepository financialSettingRepository
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.customerRepository = customerRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentFrequencyRepository = paymentFrequencyRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.financialSettingRepository = financialSettingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceivableDTO> findAllPaged(String description, PageRequest pageRequest) {
        ReceivableFilterDTO filters = new ReceivableFilterDTO();
        filters.setSearch(description);
        filters.setStatus("ALL");
        filters.setPeriodType("DUE_DATE");
        filters.setOrderBy("dueDate");
        filters.setDirection("ASC");
        return findAllPaged(filters, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceivableDTO> findAllPaged(ReceivableFilterDTO filters, PageRequest pageRequest) {
        ReceivableFilterDTO normalized = normalizeFilters(filters);

        return repository.findWithFilters(
                        normalized.getSearch(),
                        dateFilterOrDisabled(normalized.getStartDate()),
                        dateFilterOrDisabled(normalized.getEndDate()),
                        normalized.getStartDate() != null,
                        normalized.getEndDate() != null,
                        normalized.getStatus(),
                        normalized.getPeriodType(),
                        idFilterOrDisabled(normalized.getCustomerId()),
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
    public ReceivableDetailsDTO findById(Long id) {
        Receivable entity = findEntity(id);
        ReceivableDetailsDTO dto = mapper.toDetailsDTO(entity);
        fillLateCharges(entity, dto);
        return dto;
    }

    @Override
    @Transactional
    public ReceivableDTO insert(ReceivableInsertDTO dto) {
        validateCustomerOrDescription(dto);

        Receivable entity = mapper.toEntity(dto);
        applyRelations(entity, dto);
        entity.setCreatedBy(getAuthenticatedUser());

        if (entity.getPaid()) {
            entity.setPaidBy(entity.getCreatedBy());
            entity.setSubtotal(valueOrZero(entity.getAmount()));
            entity.setRemainingBalance(ZERO);
        }

        entity = repository.save(entity);
        return toDTOWithLateCharges(entity);
    }

    @Override
    @Transactional
    public ReceivableDTO update(Long id, ReceivableUpdateDTO dto) {
        validateCustomerOrDescription(dto);

        Receivable entity = findEntity(id);
        mapper.updateEntity(entity, dto);
        applyRelations(entity, dto);
        entity.setUpdatedBy(getAuthenticatedUser());

        if (entity.getPaid()) {
            entity.setPaidBy(entity.getPaidBy() == null ? entity.getUpdatedBy() : entity.getPaidBy());
            entity.setRemainingBalance(ZERO);
        }

        return toDTOWithLateCharges(repository.save(entity));
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
        Receivable entity = findEntity(id);
        BigDecimal basePaymentAmount = getBasePaymentAmount(dto);
        validatePayment(entity, basePaymentAmount);

        User user = getAuthenticatedUser();
        BigDecimal amount = valueOrZero(entity.getAmount());
        BigDecimal openAmount = getOpenAmount(entity);
        BigDecimal paidAmount = amount.subtract(openAmount);

        if (basePaymentAmount.compareTo(openAmount) == 0) {
            payTotal(entity, dto, user, paidAmount.add(basePaymentAmount));
            return toDTOWithLateCharges(repository.save(entity));
        }

        BigDecimal remaining = openAmount.subtract(basePaymentAmount);
        entity.setSubtotal(paidAmount.add(basePaymentAmount));
        entity.setRemainingBalance(remaining);
        entity.setPaid(false);
        entity.setPaymentDate(dto.getPaymentDate() == null ? LocalDate.now() : dto.getPaymentDate());
        entity.setPaymentMethod(findPaymentMethod(normalizeId(dto.getPaymentMethodId())) == null
                ? entity.getPaymentMethod()
                : findPaymentMethod(normalizeId(dto.getPaymentMethodId())));
        entity.setFee(valueOrZero(dto.getFee()));
        entity.setLateInterest(valueOrZero(dto.getLateInterest()));
        entity.setLateFee(valueOrZero(dto.getLateFee()));
        entity.setDiscount(valueOrZero(dto.getDiscount()));
        entity.setUpdatedBy(user);

        return toDTOWithLateCharges(repository.save(entity));
    }

    @Override
    @Transactional
    public List<ReceivableDTO> installment(Long id, ReceivableInstallmentDTO dto) {
        Receivable original = findEntity(id);

        if (Boolean.TRUE.equals(original.getPaid())) {
            throw new IllegalArgumentException("Não é possível parcelar conta já paga.");
        }

        validateNotInstallmented(original);

        BigDecimal total = valueOrZero(original.getAmount());
        BigDecimal base = total.divide(BigDecimal.valueOf(dto.getInstallments()), 2, RoundingMode.DOWN);
        BigDecimal accumulated = ZERO;
        List<Receivable> installments = new ArrayList<>();
        LocalDate firstDueDate = dto.getFirstDueDate() == null ? original.getDueDate() : dto.getFirstDueDate();

        for (int i = 1; i <= dto.getInstallments(); i++) {
            BigDecimal value = i == dto.getInstallments() ? total.subtract(accumulated) : base;
            accumulated = accumulated.add(value);

            Receivable installment = copyBase(original);
            installment.setId(null);
            installment.setAmount(value);
            installment.setRemainingBalance(value);
            installment.setPaid(false);
            installment.setPaymentDate(null);
            installment.setDescription(buildInstallmentDescription(original.getDescription(), i, dto.getInstallments()));
            installment.setDueDate(firstDueDate == null ? null : firstDueDate.plusMonths(i - 1L));
            installment.setParentReceivable(original);
            installment.setCreatedBy(getAuthenticatedUser());
            installments.add(installment);
        }

        original.setCanceled(true);
        original.setUpdatedBy(getAuthenticatedUser());
        repository.save(original);

        return repository.saveAll(installments).stream()
                .map(this::toDTOWithLateCharges)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReceivableReportDTO report(
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String dateType
    ) {
        List<Receivable> items = repository.findAll(filters(description, startDate, endDate, status, dateType));
        BigDecimal total = sum(items);
        BigDecimal paid = sum(items.stream().filter(item -> Boolean.TRUE.equals(item.getPaid())).collect(Collectors.toList()));
        BigDecimal open = total.subtract(paid);

        return new ReceivableReportDTO((long) items.size(), total, paid, open);
    }

    @Override
    @Transactional(readOnly = true)
    public String receipt(Long id) {
        Receivable entity = findEntity(id);
        StringBuilder builder = new StringBuilder();
        builder.append("RECIBO DE CONTA A RECEBER\n");
        builder.append("Conta: #").append(entity.getId()).append('\n');
        builder.append("Cliente: ").append(entity.getCustomer() == null ? "-" : entity.getCustomer().getName()).append('\n');
        builder.append("Descrição: ").append(entity.getDescription() == null ? "-" : entity.getDescription()).append('\n');
        builder.append("Valor: ").append(valueOrZero(entity.getAmount())).append('\n');
        builder.append("Vencimento: ").append(entity.getDueDate() == null ? "-" : entity.getDueDate()).append('\n');
        builder.append("Pagamento: ").append(entity.getPaymentDate() == null ? "-" : entity.getPaymentDate()).append('\n');
        builder.append("Status: ").append(Boolean.TRUE.equals(entity.getPaid()) ? "Pago" : "Pendente").append('\n');
        builder.append("Recebido por: ").append(entity.getPaidBy() == null ? "-" : entity.getPaidBy().getName()).append('\n');
        return builder.toString();
    }

    private ReceivableFilterDTO normalizeFilters(ReceivableFilterDTO filters) {
        ReceivableFilterDTO normalized = filters == null ? new ReceivableFilterDTO() : filters;

        normalized.setSearch(trimToNull(normalized.getSearch()));
        normalized.setStatus(normalizeStatus(normalized.getStatus()));
        normalized.setPeriodType(normalizePeriodType(normalized.getPeriodType()));
        normalized.setOrderBy(normalizeOrderBy(normalized.getOrderBy()));
        normalized.setDirection(normalizeDirection(normalized.getDirection()));
        normalized.setCustomerId(normalizeId(normalized.getCustomerId()));
        normalized.setPaymentMethodId(normalizeId(normalized.getPaymentMethodId()));
        normalized.setPaymentFrequencyId(normalizeId(normalized.getPaymentFrequencyId()));

        return normalized;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "ALL";
        }

        String value = status.trim().toUpperCase();

        if ("OPEN".equals(value)) {
            return "PENDING";
        }

        if (Arrays.asList("ALL", "PENDING", "PAID", "OVERDUE", "PARTIALLY_PAID", "CANCELED").contains(value)) {
            return value;
        }

        return "ALL";
    }

    private String normalizePeriodType(String periodType) {
        if (periodType == null || periodType.trim().isEmpty()) {
            return "DUE_DATE";
        }

        String value = periodType.trim().toUpperCase();

        if ("DUE".equals(value)) {
            return "DUE_DATE";
        }

        if ("PAYMENT".equals(value)) {
            return "PAYMENT_DATE";
        }

        if ("CREATED".equals(value)) {
            return "CREATED_DATE";
        }

        if (Arrays.asList("DUE_DATE", "PAYMENT_DATE", "CREATED_DATE").contains(value)) {
            return value;
        }

        return "DUE_DATE";
    }

    private String normalizeOrderBy(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return "dueDate";
        }

        String value = orderBy.trim();
        if (Arrays.asList("dueDate", "paymentDate", "createdDate", "amount", "description").contains(value)) {
            return value;
        }

        return "dueDate";
    }

    private String normalizeDirection(String direction) {
        if ("DESC".equalsIgnoreCase(direction)) {
            return "DESC";
        }

        return "ASC";
    }

    private Specification<Receivable> filters(
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String dateType
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (description != null && !description.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.trim().toLowerCase() + "%"));
            }

            if ("paid".equalsIgnoreCase(status)) {
                predicates.add(cb.isTrue(root.get("paid")));
            } else if ("open".equalsIgnoreCase(status)) {
                predicates.add(cb.isFalse(root.get("paid")));
            }

            if (startDate != null || endDate != null) {
                if ("created".equalsIgnoreCase(dateType)) {
                    Path<java.time.Instant> path = root.get("createdAt");

                    if (startDate != null) {
                        predicates.add(cb.greaterThanOrEqualTo(path, startDate.atStartOfDay().toInstant(ZoneOffset.UTC)));
                    }

                    if (endDate != null) {
                        predicates.add(cb.lessThan(path, endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
                    }
                } else {
                    Path<LocalDate> path = datePath(root, dateType);

                    if (startDate != null) {
                        predicates.add(cb.greaterThanOrEqualTo(path, startDate));
                    }

                    if (endDate != null) {
                        predicates.add(cb.lessThanOrEqualTo(path, endDate));
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Path<LocalDate> datePath(javax.persistence.criteria.Root<Receivable> root, String dateType) {
        if ("payment".equalsIgnoreCase(dateType)) {
            return root.get("paymentDate");
        }

        return root.get("dueDate");
    }

    private void applyRelations(Receivable entity, ReceivableSaveDTO dto) {
        entity.setCustomer(findCustomer(normalizeId(dto.getCustomerId())));
        entity.setPaymentMethod(findPaymentMethod(normalizeId(dto.getPaymentMethodId())));
        entity.setPaymentFrequency(findPaymentFrequency(normalizeId(dto.getPaymentFrequencyId())));
    }

    private void validateCustomerOrDescription(ReceivableSaveDTO dto) {
        if (normalizeId(dto.getCustomerId()) == null && (dto.getDescription() == null || dto.getDescription().trim().isEmpty())) {
            throw new IllegalArgumentException("Informe o cliente ou a descrição da conta.");
        }
    }

    private BigDecimal getBasePaymentAmount(ReceivablePaymentDTO dto) {
        BigDecimal subtotal = valueOrZero(dto.getSubtotal());
        return subtotal.compareTo(ZERO) > 0 ? subtotal : dto.getPaymentAmount();
    }

    private void validatePayment(Receivable entity, BigDecimal basePaymentAmount) {
        if (Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException("Conta já está paga.");
        }

        if (basePaymentAmount == null || basePaymentAmount.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de baixa deve ser maior que zero.");
        }

        if (basePaymentAmount.compareTo(getOpenAmount(entity)) > 0) {
            throw new IllegalArgumentException("Valor de baixa não pode ser maior que o valor da conta.");
        }
    }

    private void validateNotInstallmented(Receivable entity) {
        if (entity.getParentReceivable() != null || repository.existsByParentReceivableId(entity.getId())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.RECEIVABLE_ALREADY_INSTALLMENTED);
        }
    }

    private void payTotal(Receivable entity, ReceivablePaymentDTO dto, User user, BigDecimal paidAmount) {
        entity.setPaid(true);
        entity.setPaymentDate(dto.getPaymentDate() == null ? LocalDate.now() : dto.getPaymentDate());
        entity.setPaymentMethod(findPaymentMethod(normalizeId(dto.getPaymentMethodId())) == null
                ? entity.getPaymentMethod()
                : findPaymentMethod(normalizeId(dto.getPaymentMethodId())));
        entity.setSubtotal(paidAmount);
        entity.setFee(valueOrZero(dto.getFee()));
        entity.setLateInterest(valueOrZero(dto.getLateInterest()));
        entity.setLateFee(valueOrZero(dto.getLateFee()));
        entity.setDiscount(valueOrZero(dto.getDiscount()));
        entity.setRemainingBalance(ZERO);
        entity.setPaidBy(user);
        entity.setUpdatedBy(user);
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

    private String buildInstallmentDescription(String description, int installment, int total) {
        String base = description == null || description.trim().isEmpty() ? "Conta a receber" : description.trim();
        return base + " (" + installment + "/" + total + ")";
    }

    private BigDecimal sum(List<Receivable> items) {
        return items.stream()
                .map(item -> valueOrZero(item.getAmount()))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? ZERO : value;
    }

    private BigDecimal getOpenAmount(Receivable entity) {
        BigDecimal amount = valueOrZero(entity.getAmount());
        BigDecimal paidAmount = hasPaymentRecord(entity) ? valueOrZero(entity.getSubtotal()) : ZERO;

        if (amount.compareTo(ZERO) > 0 && paidAmount.compareTo(amount) >= 0) {
            return ZERO;
        }

        if (paidAmount.compareTo(ZERO) > 0 && paidAmount.compareTo(amount) < 0) {
            return amount.subtract(paidAmount);
        }

        BigDecimal remaining = entity.getRemainingBalance();

        if (remaining != null && remaining.compareTo(ZERO) > 0 && remaining.compareTo(amount) < 0) {
            return remaining;
        }

        return amount;
    }

    private ReceivableDTO toDTOWithLateCharges(Receivable entity) {
        ReceivableDTO dto = mapper.toDTO(entity);
        fillLateCharges(entity, dto);
        return dto;
    }

    private void fillLateCharges(Receivable entity, ReceivableDTO dto) {
        BigDecimal amount = valueOrZero(entity.getAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal openAmount = getOpenAmount(entity).setScale(2, RoundingMode.HALF_UP);
        dto.setCurrentAmountWithLateCharges(Boolean.TRUE.equals(entity.getPaid()) ? amount : openAmount);
        dto.setOverdueDays(0L);
        dto.setCalculatedLateInterest(ZERO.setScale(2, RoundingMode.HALF_UP));
        dto.setCalculatedLateFee(ZERO.setScale(2, RoundingMode.HALF_UP));

        if (!isOverdueOpenReceivable(entity)) {
            return;
        }

        long overdueDays = ChronoUnit.DAYS.between(entity.getDueDate(), LocalDate.now());
        FinancialSetting setting = financialSettingRepository
                .findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY)
                .orElseGet(FinancialSetting::new);
        BigDecimal lateFee = percentageOf(openAmount, setting.getDefaultLateFeePercent());
        BigDecimal lateInterest = percentageOf(openAmount, setting.getDefaultLateInterestPercent())
                .multiply(BigDecimal.valueOf(overdueDays))
                .setScale(2, RoundingMode.HALF_UP);

        dto.setOverdueDays(overdueDays);
        dto.setCalculatedLateInterest(lateInterest);
        dto.setCalculatedLateFee(lateFee);
        dto.setCurrentAmountWithLateCharges(openAmount.add(lateInterest).add(lateFee).setScale(2, RoundingMode.HALF_UP));
    }

    private boolean isOverdueOpenReceivable(Receivable entity) {
        return !Boolean.TRUE.equals(entity.getPaid())
                && !Boolean.TRUE.equals(entity.getCanceled())
                && entity.getDueDate() != null
                && entity.getDueDate().isBefore(LocalDate.now())
                && getOpenAmount(entity).compareTo(ZERO) > 0;
    }

    private BigDecimal percentageOf(BigDecimal amount, BigDecimal percent) {
        return amount.multiply(valueOrZero(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private boolean hasPaymentRecord(Receivable entity) {
        return Boolean.TRUE.equals(entity.getPaid()) || entity.getPaymentDate() != null;
    }

    private Long normalizeId(Long id) {
        return id == null || id <= 0 ? null : id;
    }

    private Long idFilterOrDisabled(Long id) {
        return id == null ? FILTER_ID_DISABLED : id;
    }

    private BigDecimal amountFilterOrDisabled(BigDecimal amount) {
        return amount == null ? FILTER_AMOUNT_DISABLED : amount;
    }

    private LocalDate dateFilterOrDisabled(LocalDate date) {
        return date == null ? FILTER_DATE_DISABLED : date;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private Customer findCustomer(Long id) {
        if (id == null) {
            return null;
        }

        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado. Id: " + id));
    }

    private PaymentMethod findPaymentMethod(Long id) {
        if (id == null) {
            return null;
        }

        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pagamento não encontrada. Id: " + id));
    }

    private PaymentFrequency findPaymentFrequency(Long id) {
        if (id == null) {
            return null;
        }

        return paymentFrequencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Frequência não encontrada. Id: " + id));
    }

    private User getAuthenticatedUser() {
        String username = authenticationFacade.getAuthenticatedUsername();
        if (username == null) {
            return null;
        }

        return userRepository.findByEmail(username);
    }

    private Receivable findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ReceivableErrorMessages.RECEIVABLE_NOT_FOUND + " Id: " + id));
    }
}
