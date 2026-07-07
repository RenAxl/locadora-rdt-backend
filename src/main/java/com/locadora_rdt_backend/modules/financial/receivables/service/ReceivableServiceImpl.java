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
import com.lowagie.text.DocumentException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final ReceivableFilterNormalizer filterNormalizer;
    private final ReceivableFinancialCalculator financialCalculator;
    private final ReceivableDocumentPdfService documentPdfService;
    private final Clock clock;

    public ReceivableServiceImpl(
            ReceivableRepository repository,
            ReceivableMapper mapper,
            CustomerRepository customerRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentFrequencyRepository paymentFrequencyRepository,
            UserRepository userRepository,
            AuthenticationFacade authenticationFacade,
            ReceivableFilterNormalizer filterNormalizer,
            ReceivableFinancialCalculator financialCalculator,
            ReceivableDocumentPdfService documentPdfService,
            Clock clock
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.customerRepository = customerRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentFrequencyRepository = paymentFrequencyRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.filterNormalizer = filterNormalizer;
        this.financialCalculator = financialCalculator;
        this.documentPdfService = documentPdfService;
        this.clock = clock;
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
        ReceivableFilterDTO normalized = filterNormalizer.normalize(filters);

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
        financialCalculator.fillLateCharges(entity, dto);
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
            entity.setSubtotal(financialCalculator.valueOrZero(entity.getAmount()));
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
    public List<ReceivableDTO> installment(Long id, ReceivableInstallmentDTO dto) {
        Receivable original = findEntity(id);

        if (Boolean.TRUE.equals(original.getPaid())) {
            throw new IllegalArgumentException("Não é possível parcelar conta já paga.");
        }

        validateNotInstallmented(original);

        BigDecimal total = financialCalculator.valueOrZero(original.getAmount());
        BigDecimal base = total.divide(BigDecimal.valueOf(dto.getInstallments()), 2, RoundingMode.DOWN);
        BigDecimal accumulated = ZERO;
        List<Receivable> installments = new ArrayList<>();
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

            Receivable installment = copyBase(original);
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

            installment.setParentReceivable(original);
            installment.setCreatedBy(getAuthenticatedUser());
            installments.add(installment);
        }

        original.setCanceled(true);
        original.setUpdatedBy(getAuthenticatedUser());
        repository.save(original);

        List<Receivable> savedInstallments = repository.saveAll(installments);
        List<ReceivableDTO> result = new ArrayList<>();

        for (Receivable item : savedInstallments) {
            ReceivableDTO dtoResult = toDTOWithLateCharges(item);
            result.add(dtoResult);
        }

        return result;
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
        ReceivableFilterDTO reportFilters = new ReceivableFilterDTO();
        reportFilters.setSearch(description);
        reportFilters.setStartDate(startDate);
        reportFilters.setEndDate(endDate);
        reportFilters.setStatus(status);
        reportFilters.setPeriodType(dateType);

        ReceivableFilterDTO normalized = filterNormalizer.normalize(reportFilters);
        List<Receivable> items = repository.findWithFilters(
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
                Pageable.unpaged()
        ).getContent();
        BigDecimal total = sum(items);
        List<Receivable> paidItems = new ArrayList<>();

        for (Receivable item : items) {
            if (Boolean.TRUE.equals(item.getPaid())) {
                paidItems.add(item);
            }
        }

        BigDecimal paid = sum(paidItems);
        BigDecimal open = total.subtract(paid);

        return new ReceivableReportDTO((long) items.size(), total, paid, open);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] receipt(Long id) {
        Receivable entity = findEntity(id);

        if (!Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException("Recibo disponível apenas para contas pagas.");
        }

        try {
            return documentPdfService.buildReceiptPdf(entity);
        } catch (DocumentException e) {
            throw new IllegalStateException("Erro ao gerar recibo.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] fiscalCoupon(Long id) {
        Receivable entity = findEntity(id);

        if (!Boolean.TRUE.equals(entity.getPaid())) {
            throw new IllegalArgumentException("Cupom fiscal disponível apenas para contas pagas.");
        }

        try {
            return documentPdfService.buildFiscalCouponPdf(entity);
        } catch (DocumentException e) {
            throw new IllegalStateException("Erro ao gerar cupom fiscal.", e);
        }
    }

    private void applyRelations(Receivable entity, ReceivableSaveDTO dto) {
        entity.setCustomer(findCustomer(normalizeId(dto.getCustomerId())));
        entity.setPaymentMethod(findPaymentMethod(normalizeId(dto.getPaymentMethodId())));
        entity.setPaymentFrequency(findPaymentFrequency(normalizeId(dto.getPaymentFrequencyId())));
    }

    private void validateCustomerOrDescription(ReceivableSaveDTO dto) {
        boolean hasCustomer = normalizeId(dto.getCustomerId()) != null;
        boolean hasDescription = dto.getDescription() != null && !dto.getDescription().trim().isEmpty();

        if (!hasCustomer && !hasDescription) {
            throw new IllegalArgumentException("Informe o cliente ou a descrição da conta.");
        }
    }

    private void validatePayment(Receivable entity, ReceivablePaymentDTO dto, BigDecimal paymentAmount) {
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

    private void validateNotInstallmented(Receivable entity) {
        if (entity.getParentReceivable() != null || repository.existsByParentReceivableId(entity.getId())) {
            throw new IllegalArgumentException(ReceivableErrorMessages.RECEIVABLE_ALREADY_INSTALLMENTED);
        }
    }

    private boolean isPartiallyPaid(Receivable entity) {
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

    private void payTotal(Receivable entity, ReceivablePaymentDTO dto, User user, PaymentMethod requestedPaymentMethod, BigDecimal paidAmount) {
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
        String base;

        if (description == null || description.trim().isEmpty()) {
            base = "Conta a receber";
        } else {
            base = description.trim();
        }

        return base + " (" + installment + "/" + total + ")";
    }

    private BigDecimal sum(List<Receivable> items) {
        BigDecimal total = ZERO;

        for (Receivable item : items) {
            total = total.add(financialCalculator.valueOrZero(item.getAmount()));
        }

        return total;
    }

    private ReceivableDTO toDTOWithLateCharges(Receivable entity) {
        ReceivableDTO dto = mapper.toDTO(entity);
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

    private Customer findCustomer(Long id) {
        if (id == null) {
            return null;
        }

        Optional<Customer> optionalCustomer = customerRepository.findById(id);

        if (optionalCustomer.isEmpty()) {
            throw new ResourceNotFoundException("Cliente não encontrado. Id: " + id);
        }

        return optionalCustomer.get();
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

    private Receivable findEntity(Long id) {
        Optional<Receivable> optionalReceivable = repository.findById(id);

        if (optionalReceivable.isEmpty()) {
            throw new ResourceNotFoundException(ReceivableErrorMessages.RECEIVABLE_NOT_FOUND + " Id: " + id);
        }

        return optionalReceivable.get();
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
