package com.locadora_rdt_backend.tests.modules.financial.payables.service;

import com.locadora_rdt_backend.common.exception.DatabaseException;
import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.suppliers.repository.SupplierRepository;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.repository.PaymentFrequencyRepository;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment.methods.repository.PaymentMethodRepository;
import com.locadora_rdt_backend.modules.financial.payment.settings.model.FinancialSetting;
import com.locadora_rdt_backend.modules.financial.payment.settings.repository.FinancialSettingRepository;
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
import com.locadora_rdt_backend.modules.financial.payables.service.PayableFilterNormalizer;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableFinancialCalculator;
import com.locadora_rdt_backend.modules.financial.payables.service.PayableServiceImpl;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5778")
class PayableServiceTests {

    private PayableServiceImpl service;

    @Mock
    private PayableRepository repository;

    @Mock
    private PayableMapper mapper;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentFrequencyRepository paymentFrequencyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private FinancialSettingRepository financialSettingRepository;

    private Payable entity;
    private PayableDTO dto;
    private User user;
    private Supplier supplier;
    private Employee employee;
    private PaymentMethod paymentMethod;
    private PaymentFrequency paymentFrequency;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-07-06T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        PayableFinancialCalculator financialCalculator = new PayableFinancialCalculator(financialSettingRepository, clock);
        service = new PayableServiceImpl(
                repository,
                mapper,
                supplierRepository,
                employeeRepository,
                paymentMethodRepository,
                paymentFrequencyRepository,
                userRepository,
                authenticationFacade,
                new PayableFilterNormalizer(),
                financialCalculator,
                clock
        );

        entity = new Payable();
        entity.setId(1L);
        entity.setDescription("Movie rental");
        entity.setAmount(new BigDecimal("45.90"));
        entity.setRemainingBalance(new BigDecimal("45.90"));
        entity.setDueDate(LocalDate.of(2026, 7, 1));

        dto = new PayableDTO();
        dto.setId(1L);
        dto.setDescription("Movie rental");
        dto.setAmount(new BigDecimal("45.90"));

        user = new User();
        user.setId(1L);
        user.setEmail("admin@email.com");

        supplier = new Supplier();
        supplier.setId(2L);
        supplier.setName("Fornecedor");
        supplier.setCnpj("12345678000199");
        Mockito.lenient()
                .when(supplierRepository.findById(2L))
                .thenReturn(Optional.of(supplier));

        employee = new Employee();
        employee.setId(5L);
        employee.setName("Funcionário");

        paymentMethod = new PaymentMethod();
        paymentMethod.setId(3L);
        paymentMethod.setName("Cartão");

        paymentFrequency = new PaymentFrequency();
        paymentFrequency.setId(4L);
        paymentFrequency.setFrequency("Mensal");
        paymentFrequency.setDays(30);

        FinancialSetting financialSetting = new FinancialSetting();
        financialSetting.setDefaultLateFeePercent(BigDecimal.ZERO);
        financialSetting.setDefaultLateInterestPercent(BigDecimal.ZERO);
        Mockito.lenient()
                .when(financialSettingRepository.findBySingletonKey(FinancialSetting.DEFAULT_SINGLETON_KEY))
                .thenReturn(Optional.of(financialSetting));
    }

    @Test
    void findAllPagedShouldNormalizeLegacyDescriptionSearch() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Payable> page = new PageImpl<>(List.of(entity));

        Mockito.when(repository.findWithFilters(
                eq("Movie"),
                any(),
                any(),
                any(),
                any(),
                eq("ALL"),
                eq("DUE_DATE"),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                eq("dueDate"),
                eq("ASC"),
                eq(pageRequest)
        )).thenReturn(page);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        Page<PayableDTO> result = service.findAllPaged("Movie", pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1L, result.getContent().get(0).getId());
    }

    @Test
    void findAllPagedShouldNormalizeAdvancedFilters() {
        PayableFilterDTO filters = new PayableFilterDTO();
        filters.setSearch("  Teste ");
        filters.setStatus("open");
        filters.setPeriodType("payment");
        filters.setSupplierId(0L);
        filters.setPaymentMethodId(3L);
        filters.setPaymentFrequencyId(-1L);
        filters.setOrderBy("unknown");
        filters.setDirection("desc");
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findWithFilters(
                eq("Teste"),
                any(),
                any(),
                any(),
                any(),
                eq("PENDING"),
                eq("PAYMENT_DATE"),
                eq(-1L),
                eq(-1L),
                eq(3L),
                eq(-1L),
                eq(new BigDecimal("-1")),
                eq(new BigDecimal("-1")),
                eq("dueDate"),
                eq("DESC"),
                eq(pageRequest)
        )).thenReturn(new PageImpl<>(List.of(entity)));
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        Page<PayableDTO> result = service.findAllPaged(filters, pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAllPagedShouldPassEnabledDateAndAmountFilters() {
        PayableFilterDTO filters = new PayableFilterDTO();
        filters.setStartDate(LocalDate.of(2026, 7, 1));
        filters.setEndDate(LocalDate.of(2026, 7, 31));
        filters.setMinimumAmount(new BigDecimal("10.00"));
        filters.setMaximumAmount(new BigDecimal("100.00"));
        filters.setStatus("paid");
        filters.setPeriodType("created");
        filters.setOrderBy("amount");
        filters.setDirection("ASC");
        PageRequest pageRequest = PageRequest.of(0, 10);

        Mockito.when(repository.findWithFilters(
                eq(null),
                eq(LocalDate.of(2026, 7, 1)),
                eq(LocalDate.of(2026, 7, 31)),
                eq(true),
                eq(true),
                eq("PAID"),
                eq("CREATED_DATE"),
                eq(-1L),
                eq(-1L),
                eq(-1L),
                eq(-1L),
                eq(new BigDecimal("10.00")),
                eq(new BigDecimal("100.00")),
                eq("amount"),
                eq("ASC"),
                eq(pageRequest)
        )).thenReturn(new PageImpl<>(List.of(entity)));
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        Page<PayableDTO> result = service.findAllPaged(filters, pageRequest);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findByIdShouldReturnDetailsWhenIdExists() {
        PayableDetailsDTO detailsDTO = new PayableDetailsDTO();
        detailsDTO.setId(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDetailsDTO(entity)).thenReturn(detailsDTO);

        Assertions.assertEquals(1L, service.findById(1L).getId());
    }

    @Test
    void findByIdShouldThrowWhenIdDoesNotExist() {
        Mockito.when(repository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void insertShouldSaveRelationsAndAuthenticatedUser() {
        PayableInsertDTO insertDTO = saveDTO(new PayableInsertDTO());
        insertDTO.setSupplierId(2L);
        insertDTO.setPaymentMethodId(3L);
        insertDTO.setPaymentFrequencyId(4L);

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(entity);
        Mockito.when(supplierRepository.findById(2L)).thenReturn(Optional.of(supplier));
        Mockito.when(paymentMethodRepository.findById(3L)).thenReturn(Optional.of(paymentMethod));
        Mockito.when(paymentFrequencyRepository.findById(4L)).thenReturn(Optional.of(paymentFrequency));
        mockAuthenticatedUser();
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        PayableDTO result = service.insert(insertDTO);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertSame(supplier, entity.getSupplier());
        Assertions.assertSame(paymentMethod, entity.getPaymentMethod());
        Assertions.assertSame(paymentFrequency, entity.getPaymentFrequency());
        Assertions.assertSame(user, entity.getCreatedBy());
    }

    @Test
    void insertShouldSetPaymentDataWhenAlreadyPaid() {
        PayableInsertDTO insertDTO = saveDTO(new PayableInsertDTO());
        entity.setPaid(true);

        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(entity);
        mockAuthenticatedUser();
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.insert(insertDTO);

        Assertions.assertSame(user, entity.getPaidBy());
        Assertions.assertEquals(new BigDecimal("45.90"), entity.getSubtotal());
        Assertions.assertEquals(BigDecimal.ZERO, entity.getRemainingBalance());
    }

    @Test
    void insertShouldAllowAccountWithoutSupplierAndEmployee() {
        PayableInsertDTO insertDTO = new PayableInsertDTO();
        PayableDTO dto = new PayableDTO();
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(entity);
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        PayableDTO result = service.insert(insertDTO);

        Assertions.assertSame(dto, result);
        Assertions.assertNull(entity.getSupplier());
        Assertions.assertNull(entity.getEmployee());
    }

    @Test
    void insertShouldValidateSupplierRelations() {
        PayableInsertDTO insertDTO = saveDTO(new PayableInsertDTO());
        insertDTO.setSupplierId(999L);
        Mockito.when(mapper.toEntity(insertDTO)).thenReturn(entity);
        Mockito.when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(insertDTO));
    }

    @Test
    void insertShouldValidatePaymentMethodAndFrequencyRelations() {
        PayableInsertDTO invalidPaymentMethodDTO = saveDTO(new PayableInsertDTO());
        invalidPaymentMethodDTO.setPaymentMethodId(999L);
        Mockito.when(mapper.toEntity(invalidPaymentMethodDTO)).thenReturn(entity);
        Mockito.when(paymentMethodRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(invalidPaymentMethodDTO));

        PayableInsertDTO invalidFrequencyDTO = saveDTO(new PayableInsertDTO());
        invalidFrequencyDTO.setPaymentFrequencyId(999L);
        Mockito.when(mapper.toEntity(invalidFrequencyDTO)).thenReturn(entity);
        Mockito.when(paymentFrequencyRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(invalidFrequencyDTO));
    }

    @Test
    void updateShouldModifyExistingPayable() {
        PayableUpdateDTO updateDTO = saveDTO(new PayableUpdateDTO());

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        mockAuthenticatedUser();
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        PayableDTO result = service.update(1L, updateDTO);

        Assertions.assertEquals(1L, result.getId());
        Mockito.verify(mapper).updateEntity(entity, updateDTO);
        Assertions.assertSame(user, entity.getUpdatedBy());
    }

    @Test
    void updateShouldSetPaidByWhenUpdatedPayableBecomesPaid() {
        PayableUpdateDTO updateDTO = saveDTO(new PayableUpdateDTO());
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.doAnswer(invocation -> {
            Payable target = invocation.getArgument(0);
            target.setPaid(true);
            return null;
        }).when(mapper).updateEntity(entity, updateDTO);
        mockAuthenticatedUser();
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.update(1L, updateDTO);

        Assertions.assertSame(user, entity.getPaidBy());
        Assertions.assertEquals(BigDecimal.ZERO, entity.getRemainingBalance());
    }

    @Test
    void updateShouldKeepExistingPaidByWhenUpdatedPayableIsAlreadyPaidBySomeone() {
        PayableUpdateDTO updateDTO = saveDTO(new PayableUpdateDTO());
        User paidBy = new User();
        paidBy.setId(20L);
        entity.setPaidBy(paidBy);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.doAnswer(invocation -> {
            Payable target = invocation.getArgument(0);
            target.setPaid(true);
            return null;
        }).when(mapper).updateEntity(entity, updateDTO);
        mockAuthenticatedUser();
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.update(1L, updateDTO);

        Assertions.assertSame(paidBy, entity.getPaidBy());
    }

    @Test
    void updateShouldKeepPartialPaymentStatusWhenPayableWasPartiallyPaid() {
        PayableUpdateDTO updateDTO = saveDTO(new PayableUpdateDTO());
        entity.setAmount(new BigDecimal("100.00"));
        entity.setSubtotal(new BigDecimal("40.00"));
        entity.setRemainingBalance(new BigDecimal("60.00"));
        entity.setPaid(false);
        entity.setPaymentDate(LocalDate.of(2026, 7, 1));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.doAnswer(invocation -> {
            Payable target = invocation.getArgument(0);
            target.setDescription("Movie rental updated");
            target.setPaymentDate(LocalDate.of(2026, 7, 1));
            target.setPaid(true);
            target.setRemainingBalance(BigDecimal.ZERO);
            return null;
        }).when(mapper).updateEntity(entity, updateDTO);
        mockAuthenticatedUser();
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.update(1L, updateDTO);

        Assertions.assertFalse(entity.getPaid());
        Assertions.assertEquals(new BigDecimal("60.00"), entity.getRemainingBalance());
        Assertions.assertEquals(LocalDate.of(2026, 7, 1), entity.getPaymentDate());
    }

    @Test
    void updateShouldThrowWhenPayableDoesNotExist() {
        PayableUpdateDTO updateDTO = saveDTO(new PayableUpdateDTO());
        Mockito.when(repository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(999L, updateDTO));
    }

    @Test
    void deleteShouldRemoveExistingPayable() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        Mockito.verify(repository).delete(entity);
        Mockito.verify(repository).flush();
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenIntegrityFails() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).flush();

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(1L));
    }

    @Test
    void payShouldSetTotalPaymentWithoutCreatingAnotherPayable() {
        entity.setPaymentFrequency(paymentFrequency);
        PayablePaymentDTO paymentDTO = paymentDTO(new BigDecimal("45.90"));
        paymentDTO.setPaymentMethodId(3L);
        paymentDTO.setFee(new BigDecimal("1.00"));
        paymentDTO.setLateInterest(new BigDecimal("2.00"));
        paymentDTO.setLateFee(new BigDecimal("3.00"));
        paymentDTO.setDiscount(new BigDecimal("4.00"));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(paymentMethodRepository.findById(3L)).thenReturn(Optional.of(paymentMethod));
        mockAuthenticatedUser();
        Mockito.when(repository.save(any(Payable.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.pay(1L, paymentDTO);

        Assertions.assertTrue(entity.getPaid());
        Assertions.assertEquals(BigDecimal.ZERO, entity.getRemainingBalance());
        Assertions.assertEquals(new BigDecimal("45.90"), entity.getSubtotal());
        Assertions.assertSame(paymentMethod, entity.getPaymentMethod());
        Mockito.verify(repository).save(entity);
    }

    @Test
    void payShouldKeepSamePayableWhenPaymentIsPartial() {
        entity.setAmount(new BigDecimal("30.00"));
        entity.setRemainingBalance(new BigDecimal("30.00"));
        PayablePaymentDTO paymentDTO = paymentDTO(new BigDecimal("20.00"));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.pay(1L, paymentDTO);

        ArgumentCaptor<Payable> captor = ArgumentCaptor.forClass(Payable.class);
        Mockito.verify(repository).save(captor.capture());
        Payable saved = captor.getValue();

        Assertions.assertEquals(new BigDecimal("30.00"), saved.getAmount());
        Assertions.assertEquals(new BigDecimal("20.00"), saved.getSubtotal());
        Assertions.assertEquals(new BigDecimal("10.00"), saved.getRemainingBalance());
        Assertions.assertFalse(saved.getPaid());
        Mockito.verify(repository, Mockito.never()).saveAll(any());
    }

    @Test
    void payShouldUsePaymentAmountInsteadOfSubtotal() {
        entity.setAmount(new BigDecimal("30.00"));
        entity.setRemainingBalance(new BigDecimal("30.00"));
        PayablePaymentDTO paymentDTO = paymentDTO(new BigDecimal("10.00"));
        paymentDTO.setSubtotal(new BigDecimal("30.00"));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.pay(1L, paymentDTO);

        Assertions.assertEquals(new BigDecimal("10.00"), entity.getSubtotal());
        Assertions.assertEquals(new BigDecimal("20.00"), entity.getRemainingBalance());
        Assertions.assertFalse(entity.getPaid());
    }

    @Test
    void payShouldAccumulatePartialPayments() {
        entity.setAmount(new BigDecimal("30.00"));
        entity.setRemainingBalance(new BigDecimal("10.00"));
        entity.setSubtotal(new BigDecimal("20.00"));
        PayablePaymentDTO paymentDTO = paymentDTO(new BigDecimal("5.00"));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.pay(1L, paymentDTO);

        Assertions.assertEquals(new BigDecimal("25.00"), entity.getSubtotal());
        Assertions.assertEquals(new BigDecimal("5.00"), entity.getRemainingBalance());
    }

    @Test
    void payShouldPreferPaidAmountWhenRemainingBalanceIsInconsistent() {
        entity.setAmount(new BigDecimal("150.00"));
        entity.setPaymentDate(LocalDate.of(2026, 7, 1));
        entity.setSubtotal(new BigDecimal("138.00"));
        entity.setRemainingBalance(new BigDecimal("138.00"));
        PayablePaymentDTO paymentDTO = paymentDTO(new BigDecimal("10.00"));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.pay(1L, paymentDTO);

        Assertions.assertEquals(new BigDecimal("148.00"), entity.getSubtotal());
        Assertions.assertEquals(new BigDecimal("2.00"), entity.getRemainingBalance());
    }

    @Test
    void payShouldRejectPaymentWhenPaidAmountAlreadyExceedsPayableAmount() {
        entity.setAmount(new BigDecimal("35.00"));
        entity.setPaymentDate(LocalDate.of(2026, 7, 1));
        entity.setSubtotal(new BigDecimal("41.50"));
        entity.setRemainingBalance(new BigDecimal("35.00"));
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.pay(1L, paymentDTO(new BigDecimal("1.00"))));
    }

    @Test
    void payShouldIgnoreLegacySubtotalWhenPayableHasNoPaymentRecord() {
        entity.setAmount(new BigDecimal("35.00"));
        entity.setPaymentDate(null);
        entity.setPaid(false);
        entity.setSubtotal(new BigDecimal("41.50"));
        entity.setRemainingBalance(new BigDecimal("41.50"));
        PayablePaymentDTO paymentDTO = paymentDTO(new BigDecimal("35.00"));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.pay(1L, paymentDTO);

        Assertions.assertTrue(entity.getPaid());
        Assertions.assertEquals(new BigDecimal("35.00"), entity.getSubtotal());
        Assertions.assertEquals(BigDecimal.ZERO, entity.getRemainingBalance());
    }

    @Test
    void payShouldIgnoreZeroRemainingBalanceWhenPayableIsNotPaid() {
        entity.setAmount(new BigDecimal("35.00"));
        entity.setPaid(false);
        entity.setSubtotal(null);
        entity.setRemainingBalance(BigDecimal.ZERO);
        PayablePaymentDTO paymentDTO = paymentDTO(new BigDecimal("35.00"));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        service.pay(1L, paymentDTO);

        Assertions.assertTrue(entity.getPaid());
        Assertions.assertEquals(new BigDecimal("35.00"), entity.getSubtotal());
        Assertions.assertEquals(BigDecimal.ZERO, entity.getRemainingBalance());
    }

    @Test
    void payShouldValidatePayment() {
        entity.setPaid(true);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.pay(1L, paymentDTO(BigDecimal.ONE)));

        entity.setPaid(false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.pay(1L, paymentDTO(BigDecimal.ZERO)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.pay(1L, paymentDTO(new BigDecimal("100.00"))));
    }

    @Test
    void installmentShouldCreateInstallmentsAndCancelOriginal() {
        entity.setAmount(new BigDecimal("100.00"));
        PayableInstallmentDTO installmentDTO = new PayableInstallmentDTO();
        installmentDTO.setInstallments(3);
        installmentDTO.setFirstDueDate(LocalDate.of(2026, 8, 1));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(repository.existsByParentPayableId(1L)).thenReturn(false);
        Mockito.when(repository.save(entity)).thenReturn(entity);
        Mockito.when(repository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(any(Payable.class))).thenReturn(dto);

        List<PayableDTO> result = service.installment(1L, installmentDTO);

        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(entity.getCanceled());
        Mockito.verify(repository).save(entity);
    }

    @Test
    void installmentShouldThrowWhenPayableIsPaidAlreadyHasInstallmentsOrIsInstallment() {
        PayableInstallmentDTO installmentDTO = new PayableInstallmentDTO();
        installmentDTO.setInstallments(2);

        entity.setPaid(true);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(entity));
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.installment(1L, installmentDTO));

        entity.setPaid(false);
        Mockito.when(repository.existsByParentPayableId(1L)).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.installment(1L, installmentDTO));

        Payable parent = new Payable();
        parent.setId(10L);
        entity.setParentPayable(parent);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.installment(1L, installmentDTO));
        Mockito.verify(repository, Mockito.never()).saveAll(any());
    }

    @Test
    void reportShouldReturnTotals() {
        Payable paid = new Payable();
        paid.setAmount(new BigDecimal("10.00"));
        paid.setPaid(true);
        Payable open = new Payable();
        open.setAmount(new BigDecimal("20.00"));
        open.setPaid(false);
        Mockito.when(repository.findWithFilters(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(new PageImpl<>(List.of(paid, open)));

        PayableReportDTO result = service.report("Movie", LocalDate.now(), LocalDate.now(), "paid", "created");

        Assertions.assertEquals(2L, result.getTotalItems());
        Assertions.assertEquals(new BigDecimal("30.00"), result.getTotalAmount());
        Assertions.assertEquals(new BigDecimal("10.00"), result.getPaidAmount());
        Assertions.assertEquals(new BigDecimal("20.00"), result.getOpenAmount());
    }

    private void mockAuthenticatedUser() {
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("admin@email.com");
        Mockito.when(userRepository.findByEmail("admin@email.com")).thenReturn(user);
    }

    private <T extends PayableInsertDTO> T saveDTO(T dto) {
        dto.setDescription("Movie rental");
        dto.setAmount(new BigDecimal("45.90"));
        dto.setSupplierId(2L);
        return dto;
    }

    private PayableUpdateDTO saveDTO(PayableUpdateDTO dto) {
        dto.setDescription("Movie rental");
        dto.setAmount(new BigDecimal("45.90"));
        dto.setSupplierId(2L);
        return dto;
    }

    private PayablePaymentDTO paymentDTO(BigDecimal amount) {
        PayablePaymentDTO paymentDTO = new PayablePaymentDTO();
        paymentDTO.setPaymentAmount(amount);
        paymentDTO.setPaymentDate(LocalDate.of(2026, 7, 1));
        return paymentDTO;
    }
}
