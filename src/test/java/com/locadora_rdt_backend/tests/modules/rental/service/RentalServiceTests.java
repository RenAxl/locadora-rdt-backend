package com.locadora_rdt_backend.tests.modules.rental.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.model.Address;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment.methods.repository.PaymentMethodRepository;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.inventory.items.repository.ItemRepository;
import com.locadora_rdt_backend.modules.rental.dto.RentalDTO;
import com.locadora_rdt_backend.modules.rental.dto.RentalDetailsDTO;
import com.locadora_rdt_backend.modules.rental.dto.RentalItemSaveDTO;
import com.locadora_rdt_backend.modules.rental.dto.RentalSaveDTO;
import com.locadora_rdt_backend.modules.rental.mapper.RentalMapper;
import com.locadora_rdt_backend.modules.rental.model.Rental;
import com.locadora_rdt_backend.modules.rental.model.RentalItem;
import com.locadora_rdt_backend.modules.rental.model.ItemUnit;
import com.locadora_rdt_backend.modules.rental.repository.RentalItemRepository;
import com.locadora_rdt_backend.modules.rental.repository.RentalRepository;
import com.locadora_rdt_backend.modules.rental.repository.ItemUnitRepository;
import com.locadora_rdt_backend.modules.rental.repository.RentalItemUnitRepository;
import com.locadora_rdt_backend.modules.rental.repository.RentalStatusHistoryRepository;
import com.locadora_rdt_backend.modules.rental.service.RentalServiceImpl;
import com.locadora_rdt_backend.modules.rentaltypes.model.RentalType;
import com.locadora_rdt_backend.modules.rentaltypes.repository.RentalTypeRepository;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S5778")
class RentalServiceTests {

    @InjectMocks
    private RentalServiceImpl service;

    @Mock private RentalRepository repository;
    @Mock private RentalItemRepository itemRepository;
    @Mock private ItemUnitRepository itemUnitRepository;
    @Mock private RentalItemUnitRepository rentalItemUnitRepository;
    @Mock private RentalStatusHistoryRepository statusHistoryRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private RentalTypeRepository rentalTypeRepository;
    @Mock private ItemRepository inventoryItemRepository;
    @Mock private PaymentMethodRepository paymentMethodRepository;
    @Mock private RentalMapper mapper;
    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private UserRepository userRepository;

    private Long existingId;
    private Long nonExistingId;
    private Customer customer;
    private RentalType rentalType;
    private PaymentMethod paymentMethod;
    private Item item;
    private Rental rental;
    private RentalDTO rentalDTO;
    private RentalDetailsDTO detailsDTO;
    private RentalSaveDTO saveDTO;
    private RentalItemSaveDTO itemDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;

        customer = createCustomer();
        rentalType = createRentalType();
        paymentMethod = createPaymentMethod();
        item = createItem();
        rental = createRental();
        rentalDTO = new RentalDTO();
        rentalDTO.setId(existingId);
        detailsDTO = new RentalDetailsDTO();
        detailsDTO.setId(existingId);
        itemDTO = createItemDTO();
        saveDTO = createSaveDTO();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAllPagedShouldReturnPageAndNormalizeNullFilters() {
        PageRequest request = PageRequest.of(0, 10);
        PageImpl<Rental> page = new PageImpl<>(List.of(rental));
        Mockito.when(repository.findFiltered(
                "", "", "", -1L,
                Instant.parse("1900-01-01T00:00:00Z"),
                Instant.parse("2999-12-31T23:59:59Z"), request
        )).thenReturn(page);
        Mockito.when(mapper.toDTO(rental)).thenReturn(rentalDTO);

        Page<RentalDTO> result = service.findAllPaged(null, null, null, null, null, null, request);

        Assertions.assertFalse(result.isEmpty());
        Mockito.verify(repository).findFiltered(
                "", "", "", -1L,
                Instant.parse("1900-01-01T00:00:00Z"),
                Instant.parse("2999-12-31T23:59:59Z"), request
        );
    }

    @Test
    void findAllPagedShouldTrimFiltersAndKeepInformedValues() {
        PageRequest request = PageRequest.of(0, 10);
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-12-31T23:59:59Z");
        Mockito.when(repository.findFiltered("LOC", "Renan", "DRAFT", 2L, from, to, request))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<RentalDTO> result = service.findAllPaged(
                " LOC ", " Renan ", " DRAFT ", 2L, from, to, request
        );

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(repository).findFiltered("LOC", "Renan", "DRAFT", 2L, from, to, request);
    }

    @Test
    void findByIdShouldReturnDetailsWhenRentalExists() {
        RentalItem rentalItem = new RentalItem();
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));
        Mockito.when(itemRepository.findByRentalIdOrderById(existingId)).thenReturn(List.of(rentalItem));
        Mockito.when(mapper.toDetailsDTO(rental, List.of(rentalItem))).thenReturn(detailsDTO);

        RentalDetailsDTO result = service.findById(existingId);

        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    void findByIdShouldThrowWhenRentalDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    void findCurrentCustomerShouldReturnAuthenticatedCustomer() {
        mockAuthenticatedCustomer();
        User user = createUser();
        Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(user);

        CustomerDTO result = service.findCurrentCustomer();

        Assertions.assertEquals(customer.getId(), result.getId());
        Assertions.assertEquals(customer.getName(), result.getName());
        Assertions.assertEquals(customer.getCpf(), result.getCpf());
        Assertions.assertEquals(customer.getEmail(), result.getEmail());
        Assertions.assertEquals(customer.getPhone(), result.getPhone());
        Assertions.assertEquals(user.getAddress().getStreet(), result.getAddress().getStreet());
        Assertions.assertEquals(user.getAddress().getNumber(), result.getAddress().getNumber());
        Assertions.assertEquals(user.getAddress().getZipCode(), result.getAddress().getZipCode());
        Assertions.assertTrue(result.getActive());
    }

    @Test
    void findCurrentCustomerShouldThrowWhenAuthenticatedUserDoesNotExist() {
        mockAuthenticatedCustomer();
        Mockito.when(userRepository.findByEmail("user@email.com")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findCurrentCustomer());
    }

    @Test
    void findCurrentCustomerShouldThrowWhenCustomerDoesNotExist() {
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("user@email.com");
        Mockito.when(customerRepository.findByEmail("user@email.com")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findCurrentCustomer());
    }

    @Test
    void findCurrentCustomerShouldThrowWhenCustomerIsInactive() {
        customer.setActive(false);
        mockAuthenticatedCustomer();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.findCurrentCustomer());
    }

    @Test
    void insertShouldCreateDraftAndCalculateValues() {
        saveDTO.setPaymentMethodId(paymentMethod.getId());
        saveDTO.setDiscount(new BigDecimal("5.00"));
        saveDTO.setShippingFee(new BigDecimal("8.00"));
        saveDTO.setAdditionalFee(new BigDecimal("2.00"));
        saveDTO.setDownPayment(new BigDecimal("10.00"));
        itemDTO.setDiscount(new BigDecimal("3.00"));
        itemDTO.setAdditionalFee(new BigDecimal("1.00"));
        mockInsertDependencies();
        Mockito.when(paymentMethodRepository.findById(paymentMethod.getId())).thenReturn(Optional.of(paymentMethod));
        Mockito.when(repository.save(Mockito.any(Rental.class))).thenAnswer(invocation -> {
            Rental saved = invocation.getArgument(0);
            saved.setId(existingId);
            return saved;
        });
        Mockito.when(mapper.toDTO(Mockito.any(Rental.class))).thenReturn(rentalDTO);

        RentalDTO result = service.insert(saveDTO);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        Mockito.verify(repository).save(rentalCaptor.capture());
        Rental savedRental = rentalCaptor.getValue();
        Assertions.assertEquals("DRAFT", savedRental.getStatus());
        Assertions.assertTrue(savedRental.getRentalNumber().startsWith("LOC-"));
        Assertions.assertNotNull(savedRental.getRentalDate());
        Assertions.assertEquals(new BigDecimal("38.00"), savedRental.getSubtotal());
        Assertions.assertEquals(new BigDecimal("1.90"), savedRental.getDiscount());
        Assertions.assertEquals(new BigDecimal("46.10"), savedRental.getTotalAmount());
        Assertions.assertEquals(new BigDecimal("36.10"), savedRental.getRemainingAmount());
        Assertions.assertEquals("user@email.com", savedRental.getCreatedBy());
        Assertions.assertEquals(existingId, result.getId());
        Mockito.verify(itemRepository).save(Mockito.any(RentalItem.class));
    }

    @Test
    void insertShouldUseItemPriceAndZeroValuesWhenOptionalValuesAreNull() {
        itemDTO.setUnitPrice(null);
        itemDTO.setDiscount(null);
        itemDTO.setAdditionalFee(null);
        saveDTO.setDiscount(null);
        saveDTO.setShippingFee(null);
        saveDTO.setAdditionalFee(null);
        saveDTO.setDownPayment(new BigDecimal("100.00"));
        mockInsertDependencies();
        Mockito.when(repository.save(Mockito.any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(Mockito.any(Rental.class))).thenReturn(rentalDTO);

        service.insert(saveDTO);

        ArgumentCaptor<RentalItem> itemCaptor = ArgumentCaptor.forClass(RentalItem.class);
        Mockito.verify(itemRepository).save(itemCaptor.capture());
        Assertions.assertEquals(new BigDecimal("40.00"), itemCaptor.getValue().getSubtotal());
        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        Mockito.verify(repository).save(rentalCaptor.capture());
        Assertions.assertEquals(
                0,
                new BigDecimal("0.00").compareTo(rentalCaptor.getValue().getRemainingAmount())
        );
    }

    @Test
    void insertShouldApplyFivePercentDiscountForBankSlip() {
        paymentMethod.setName("Boleto Bancário");
        saveDTO.setPaymentMethodId(paymentMethod.getId());
        saveDTO.setDiscount(new BigDecimal("99.00"));
        mockInsertDependencies();
        Mockito.when(paymentMethodRepository.findById(paymentMethod.getId())).thenReturn(Optional.of(paymentMethod));
        Mockito.when(repository.save(Mockito.any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(Mockito.any(Rental.class))).thenReturn(rentalDTO);

        service.insert(saveDTO);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        Mockito.verify(repository).save(rentalCaptor.capture());
        Assertions.assertEquals(new BigDecimal("2.00"), rentalCaptor.getValue().getDiscount());
        Assertions.assertEquals(new BigDecimal("38.00"), rentalCaptor.getValue().getTotalAmount());
    }

    @Test
    void insertShouldAcceptNullItemList() {
        saveDTO.setItems(null);
        mockBasicInsertDependencies();
        Mockito.when(repository.save(Mockito.any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(Mockito.any(Rental.class))).thenReturn(rentalDTO);

        Assertions.assertDoesNotThrow(() -> service.insert(saveDTO));
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void insertShouldThrowWhenRentalTypeDoesNotExist() {
        mockAuthenticatedCustomer();
        Mockito.when(rentalTypeRepository.findById(rentalType.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenRentalTypeIsInactive() {
        rentalType.setActive(false);
        mockAuthenticatedCustomer();
        Mockito.when(rentalTypeRepository.findById(rentalType.getId())).thenReturn(Optional.of(rentalType));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenExpectedReturnDateIsBeforeStartDate() {
        saveDTO.setExpectedReturnDate(saveDTO.getStartDate().minusSeconds(1));
        mockBasicInsertDependencies();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenPaymentMethodDoesNotExist() {
        saveDTO.setPaymentMethodId(99L);
        mockBasicInsertDependencies();
        Mockito.when(paymentMethodRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenItemIsDuplicated() {
        RentalItemSaveDTO duplicate = createItemDTO();
        saveDTO.setItems(List.of(itemDTO, duplicate));
        mockBasicInsertDependencies();
        Mockito.when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenQuantityIsNull() {
        itemDTO.setQuantity(null);
        mockBasicInsertDependencies();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenQuantityIsZero() {
        itemDTO.setQuantity(0);
        mockBasicInsertDependencies();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenItemDoesNotExist() {
        mockBasicInsertDependencies();
        Mockito.when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenItemIsInactive() {
        item.setActive(false);
        mockInsertDependencies();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenItemSubtotalIsNegative() {
        itemDTO.setDiscount(new BigDecimal("100.00"));
        mockInsertDependencies();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenRentalTotalIsNegative() {
        saveDTO.setDiscount(new BigDecimal("100.00"));
        mockInsertDependencies();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenPriceChangesWithoutAuthentication() {
        itemDTO.setUnitPrice(new BigDecimal("30.00"));
        mockInsertDependencies();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldThrowWhenPriceChangesWithoutRequiredAuthority() {
        setAuthentication("OTHER_PERMISSION");
        itemDTO.setUnitPrice(new BigDecimal("30.00"));
        mockInsertDependencies();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.insert(saveDTO));
    }

    @Test
    void insertShouldAllowPriceChangeWithPriceAuthority() {
        setAuthentication("RENTAL_PRICE_CHANGE");
        itemDTO.setUnitPrice(new BigDecimal("30.00"));
        mockInsertDependencies();
        Mockito.when(repository.save(Mockito.any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(Mockito.any(Rental.class))).thenReturn(rentalDTO);

        Assertions.assertDoesNotThrow(() -> service.insert(saveDTO));
    }

    @Test
    void insertShouldAllowPriceChangeForAdministrator() {
        setAuthentication("ROLE_ADMINISTRADOR");
        itemDTO.setUnitPrice(new BigDecimal("30.00"));
        mockInsertDependencies();
        Mockito.when(repository.save(Mockito.any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(mapper.toDTO(Mockito.any(Rental.class))).thenReturn(rentalDTO);

        Assertions.assertDoesNotThrow(() -> service.insert(saveDTO));
    }

    @Test
    void updateShouldUpdateDraftAndReplaceItems() {
        mockUpdateDependencies();
        Mockito.when(repository.save(rental)).thenReturn(rental);
        Mockito.when(mapper.toDTO(rental)).thenReturn(rentalDTO);

        RentalDTO result = service.update(existingId, saveDTO);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("user@email.com", rental.getUpdatedBy());
        Mockito.verify(itemRepository).deleteByRentalId(existingId);
        Mockito.verify(itemRepository).flush();
        Mockito.verify(itemRepository).save(Mockito.any(RentalItem.class));
    }

    @Test
    void updateShouldThrowWhenRentalDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, saveDTO));
    }

    @Test
    void updateShouldThrowWhenRentalIsNotDraft() {
        rental.setStatus("CONFIRMED");
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(existingId, saveDTO));
    }

    @Test
    void updateShouldThrowWhenCustomerDoesNotExist() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("user@email.com");
        Mockito.when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(existingId, saveDTO));
    }

    @Test
    void confirmShouldConfirmDraft() {
        RentalItem rentalItem = new RentalItem();
        rentalItem.setId(1L);
        rentalItem.setItem(item);
        rentalItem.setQuantity(1);
        ItemUnit itemUnit = new ItemUnit();
        itemUnit.setId(1L);
        itemUnit.setItem(item);
        itemUnit.setAssetCode("CONTROLE-001");
        itemUnit.setStatus("AVAILABLE");
        itemUnit.setActive(true);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));
        Mockito.when(itemRepository.findByRentalIdOrderById(existingId)).thenReturn(List.of(rentalItem));
        Mockito.when(itemUnitRepository.findAvailableForReservation(Mockito.eq(item.getId()), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(itemUnit));
        Mockito.when(rentalItemUnitRepository.countByRentalItemIdAndStatusIn(Mockito.eq(rentalItem.getId()), Mockito.anyList()))
                .thenReturn(1L);
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("user@email.com");
        Mockito.when(repository.save(rental)).thenReturn(rental);
        Mockito.when(mapper.toDTO(rental)).thenReturn(rentalDTO);

        RentalDTO result = service.confirm(existingId);

        Assertions.assertEquals(existingId, result.getId());
        Assertions.assertEquals("CONFIRMED", rental.getStatus());
        Assertions.assertEquals("user@email.com", rental.getUpdatedBy());
    }

    @Test
    void confirmShouldThrowWhenCustomerIsNull() {
        rental.setCustomer(null);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.confirm(existingId));
    }

    @Test
    void confirmShouldThrowWhenCustomerIsInactive() {
        customer.setActive(false);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.confirm(existingId));
    }

    @Test
    void confirmShouldThrowWhenRentalHasNoItems() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));
        Mockito.when(itemRepository.findByRentalIdOrderById(existingId)).thenReturn(Collections.emptyList());

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.confirm(existingId));
    }

    @Test
    void confirmShouldThrowWhenRentalIsNotDraft() {
        rental.setStatus("CONFIRMED");
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.confirm(existingId));
    }

    @Test
    void confirmShouldThrowWhenRentalDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.confirm(nonExistingId));
    }

    @Test
    void deleteShouldDeleteDraftAndItsItems() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
        Mockito.verify(itemRepository).deleteByRentalId(existingId);
        Mockito.verify(repository).delete(rental);
    }

    @Test
    void deleteShouldThrowWhenRentalIsNotDraft() {
        rental.setStatus("CONFIRMED");
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(existingId));
    }

    @Test
    void deleteShouldThrowWhenRentalDoesNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }

    private void mockAuthenticatedCustomer() {
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("user@email.com");
        Mockito.when(customerRepository.findByEmail("user@email.com")).thenReturn(customer);
    }

    private void mockBasicInsertDependencies() {
        mockAuthenticatedCustomer();
        Mockito.when(rentalTypeRepository.findById(rentalType.getId())).thenReturn(Optional.of(rentalType));
    }

    private void mockInsertDependencies() {
        mockBasicInsertDependencies();
        Mockito.when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    }

    private void mockUpdateDependencies() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(rental));
        Mockito.when(authenticationFacade.getAuthenticatedUsername()).thenReturn("user@email.com");
        Mockito.when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Mockito.when(rentalTypeRepository.findById(rentalType.getId())).thenReturn(Optional.of(rentalType));
        Mockito.when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
    }

    private void setAuthentication(String authority) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user@email.com",
                "password",
                List.of(new SimpleGrantedAuthority(authority))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Customer createCustomer() {
        Customer entity = new Customer();
        entity.setId(1L);
        entity.setName("Renan");
        entity.setCpf("11111111111");
        entity.setEmail("user@email.com");
        entity.setPhone("31999999999");
        Address address = new Address();
        address.setStreet("Rua A");
        address.setNumber("100");
        address.setNeighborhood("Centro");
        address.setCity("Belo Horizonte");
        address.setState("MG");
        address.setZipCode("30100-000");
        entity.setAddress(address);
        entity.setActive(true);
        return entity;
    }

    private User createUser() {
        User entity = new User();
        entity.setId(1L);
        entity.setName("Renan");
        entity.setEmail("user@email.com");
        com.locadora_rdt_backend.modules.users.model.Address address =
                new com.locadora_rdt_backend.modules.users.model.Address();
        address.setStreet("Rua do Usuário");
        address.setNumber("510");
        address.setNeighborhood("Bairro do Usuário");
        address.setCity("Belo Horizonte");
        address.setState("MG");
        address.setZipCode("30664-790");
        entity.setAddress(address);
        entity.setActive(true);
        return entity;
    }

    private RentalType createRentalType() {
        RentalType entity = new RentalType();
        entity.setId(1L);
        entity.setName("Diária");
        entity.setType("DIA");
        entity.setActive(true);
        return entity;
    }

    private PaymentMethod createPaymentMethod() {
        PaymentMethod entity = new PaymentMethod();
        entity.setId(1L);
        entity.setName("Pix");
        return entity;
    }

    private Item createItem() {
        Item entity = new Item();
        entity.setId(1L);
        entity.setName("Controle");
        entity.setPrice(new BigDecimal("20.00"));
        entity.setActive(true);
        return entity;
    }

    private Rental createRental() {
        Rental entity = new Rental();
        entity.setId(existingId);
        entity.setRentalNumber("LOC-1");
        entity.setStatus("DRAFT");
        entity.setCustomer(customer);
        entity.setRentalType(rentalType);
        entity.setRentalDate(Instant.parse("2026-07-15T10:00:00Z"));
        entity.setStartDate(Instant.parse("2026-07-16T10:00:00Z"));
        entity.setExpectedReturnDate(Instant.parse("2026-07-17T10:00:00Z"));
        entity.setSubtotal(new BigDecimal("40.00"));
        entity.setDiscount(BigDecimal.ZERO);
        entity.setShippingFee(BigDecimal.ZERO);
        entity.setAdditionalFee(BigDecimal.ZERO);
        entity.setTotalAmount(new BigDecimal("40.00"));
        entity.setDownPayment(BigDecimal.ZERO);
        entity.setRemainingAmount(new BigDecimal("40.00"));
        entity.setActive(true);
        return entity;
    }

    private RentalItemSaveDTO createItemDTO() {
        RentalItemSaveDTO dto = new RentalItemSaveDTO();
        dto.setItemId(item.getId());
        dto.setQuantity(2);
        dto.setUnitPrice(new BigDecimal("20.00"));
        dto.setDiscount(BigDecimal.ZERO);
        dto.setAdditionalFee(BigDecimal.ZERO);
        return dto;
    }

    private RentalSaveDTO createSaveDTO() {
        RentalSaveDTO dto = new RentalSaveDTO();
        dto.setCustomerId(customer.getId());
        dto.setRentalTypeId(rentalType.getId());
        dto.setStartDate(Instant.parse("2026-07-16T10:00:00Z"));
        dto.setExpectedReturnDate(Instant.parse("2026-07-17T10:00:00Z"));
        dto.setDiscount(BigDecimal.ZERO);
        dto.setShippingFee(BigDecimal.ZERO);
        dto.setAdditionalFee(BigDecimal.ZERO);
        dto.setDownPayment(BigDecimal.ZERO);
        dto.setDeliveryAddress("Rua A, 100");
        dto.setNotes("Teste");
        dto.setItems(List.of(itemDTO));
        return dto;
    }
}
