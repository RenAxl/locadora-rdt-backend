package com.locadora_rdt_backend.modules.rental.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.inventory.items.repository.ItemRepository;
import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockbalances.repository.StockBalanceRepository;
import com.locadora_rdt_backend.modules.rental.dto.*;
import com.locadora_rdt_backend.modules.rental.mapper.RentalMapper;
import com.locadora_rdt_backend.modules.rental.model.Rental;
import com.locadora_rdt_backend.modules.rental.model.RentalItem;
import com.locadora_rdt_backend.modules.rental.model.ItemUnit;
import com.locadora_rdt_backend.modules.rental.model.RentalItemUnit;
import com.locadora_rdt_backend.modules.rental.model.RentalItemUnitStatus;
import com.locadora_rdt_backend.modules.rental.model.RentalStatusHistory;
import com.locadora_rdt_backend.modules.rental.repository.RentalItemRepository;
import com.locadora_rdt_backend.modules.rental.repository.RentalRepository;
import com.locadora_rdt_backend.modules.rental.repository.ItemUnitRepository;
import com.locadora_rdt_backend.modules.rental.repository.RentalItemUnitRepository;
import com.locadora_rdt_backend.modules.rental.repository.RentalStatusHistoryRepository;
import com.locadora_rdt_backend.modules.rentaltypes.model.RentalType;
import com.locadora_rdt_backend.modules.rentaltypes.repository.RentalTypeRepository;
import com.locadora_rdt_backend.modules.users.model.User;
import com.locadora_rdt_backend.modules.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RentalServiceImpl implements RentalService {
    private static final String RENTED = "RENTED";
    private static final String DELIVERED = "DELIVERED";
    private static final String AVAILABLE = "AVAILABLE";
    private static final String RESERVED = "RESERVED";
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final Instant FIRST_DATE = Instant.parse("1900-01-01T00:00:00Z");
    private static final Instant LAST_DATE = Instant.parse("2999-12-31T23:59:59Z");

    private final RentalRepository repository;
    private final RentalItemRepository itemRepository;
    private final ItemUnitRepository itemUnitRepository;
    private final RentalItemUnitRepository rentalItemUnitRepository;
    private final RentalStatusHistoryRepository statusHistoryRepository;
    private final CustomerRepository customerRepository;
    private final RentalTypeRepository rentalTypeRepository;
    private final ItemRepository inventoryItemRepository;
    private final StockBalanceRepository stockBalanceRepository;
    private final RentalMapper mapper;
    private final RentalFinancialCalculator financialCalculator;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;

    public RentalServiceImpl(RentalRepository repository, RentalItemRepository itemRepository,
            ItemUnitRepository itemUnitRepository, RentalItemUnitRepository rentalItemUnitRepository,
            RentalStatusHistoryRepository statusHistoryRepository,
            CustomerRepository customerRepository, RentalTypeRepository rentalTypeRepository,
            ItemRepository inventoryItemRepository,
            StockBalanceRepository stockBalanceRepository,
            RentalMapper mapper, RentalFinancialCalculator financialCalculator,
            AuthenticationFacade authenticationFacade, UserRepository userRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.itemUnitRepository = itemUnitRepository;
        this.rentalItemUnitRepository = rentalItemUnitRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.customerRepository = customerRepository;
        this.rentalTypeRepository = rentalTypeRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.stockBalanceRepository = stockBalanceRepository;
        this.mapper = mapper;
        this.financialCalculator = financialCalculator;
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalDTO> findAllPaged(String number, String customer, String status, Long rentalTypeId,
            Instant dateFrom, Instant dateTo, PageRequest pageRequest) {
        Long typeId = rentalTypeId;
        if (typeId == null) {
            typeId = -1L;
        }

        Instant initialDate = dateFrom;
        if (initialDate == null) {
            initialDate = FIRST_DATE;
        }

        Instant finalDate = dateTo;
        if (finalDate == null) {
            finalDate = LAST_DATE;
        }

        String rentalNumber = text(number);
        String customerName = text(customer);
        String rentalStatus = text(status);
        Page<Rental> rentals = repository.findFiltered(rentalNumber, customerName, rentalStatus,
                typeId, initialDate, finalDate, pageRequest);
        return rentals.map(rental -> {
            List<RentalItem> items = itemRepository.findByRentalIdOrderById(rental.getId());
            RentalDTO dto = mapper.toDTO(rental);
            financialCalculator.fillLateFee(rental, items, dto);
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public RentalDetailsDTO findById(Long id) {
        Rental rental = findEntity(id);
        List<RentalItem> items = itemRepository.findByRentalIdOrderById(id);
        RentalDetailsDTO result = mapper.toDetailsDTO(rental, items);
        financialCalculator.fillLateFee(rental, items, result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO findCurrentCustomer() {
        Customer customer = findAuthenticatedCustomer();
        String username = authenticationFacade.getAuthenticatedUsername();
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário autenticado não encontrado.");
        }
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setCpf(customer.getCpf());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(toCustomerAddress(user.getAddress()));
        dto.setActive(customer.getActive());
        return dto;
    }

    private com.locadora_rdt_backend.modules.customers.model.Address toCustomerAddress(
            com.locadora_rdt_backend.modules.users.model.Address userAddress) {
        if (userAddress == null) {
            return null;
        }
        com.locadora_rdt_backend.modules.customers.model.Address address =
                new com.locadora_rdt_backend.modules.customers.model.Address();
        address.setStreet(userAddress.getStreet());
        address.setNumber(userAddress.getNumber());
        address.setComplement(userAddress.getComplement());
        address.setNeighborhood(userAddress.getNeighborhood());
        address.setCity(userAddress.getCity());
        address.setState(userAddress.getState());
        address.setZipCode(userAddress.getZipCode());
        return address;
    }

    @Override
    @Transactional
    public RentalDTO insert(RentalSaveDTO dto) {
        Rental rental = new Rental();
        rental.setRentalNumber(generateNumber());
        rental.setStatus(RENTED);
        rental.setRentalDate(Instant.now());
        rental.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
        fillRental(rental, dto, true);
        Rental savedRental = repository.save(rental);
        List<RentalItem> items = saveItems(savedRental, dto.getItems());
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um item.");
        }
        for (RentalItem rentalItem : items) {
            reserveUnits(rentalItem);
        }
        registerHistory(savedRental, null, RENTED, "Locação realizada e unidades reservadas.");
        RentalDTO result = mapper.toDTO(savedRental);
        return result;
    }

    @Override
    @Transactional
    public RentalDTO update(Long id, RentalSaveDTO dto) {
        Rental rental = findEntity(id);
        requireDraft(rental);
        rental.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        fillRental(rental, dto, false);
        itemRepository.deleteByRentalId(id);
        itemRepository.flush();
        saveItems(rental, dto.getItems());
        Rental savedRental = repository.save(rental);
        RentalDTO result = mapper.toDTO(savedRental);
        return result;
    }

    @Override
    @Transactional
    public RentalDTO confirm(Long id) {
        Rental rental = findEntity(id);
        if (!RENTED.equals(rental.getStatus())) {
            throw new IllegalArgumentException("A locação não está alugada.");
        }
        return mapper.toDTO(rental);
    }

    @Override
    @Transactional
    public RentalDTO start(Long id) {
        Rental rental = findEntity(id);
        if (!RENTED.equals(rental.getStatus())) {
            throw new IllegalArgumentException("Somente uma locação alugada pode ser entregue.");
        }

        List<RentalItemUnit> units = rentalItemUnitRepository.findByRentalItemRentalIdOrderById(id);
        validateReservedUnitQuantity(id, units);
        Instant now = Instant.now();

        for (RentalItemUnit rentalItemUnit : units) {
            if (rentalItemUnit.getStatus() != RentalItemUnitStatus.RESERVED) {
                throw new IllegalArgumentException("Todas as unidades devem estar reservadas antes de iniciar a locação.");
            }
            ItemUnit itemUnit = rentalItemUnit.getItemUnit();
            if (!RESERVED.equals(itemUnit.getStatus())) {
                throw new IllegalArgumentException("A unidade " + itemUnit.getAssetCode() + " não está reservada.");
            }
            itemUnit.setStatus(RENTED);
            itemUnit.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
            rentalItemUnit.setStatus(RentalItemUnitStatus.DELIVERED);
            rentalItemUnit.setDeliveredAt(now);
            rentalItemUnit.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
            itemUnitRepository.save(itemUnit);
            rentalItemUnitRepository.save(rentalItemUnit);
        }

        List<RentalItem> rentalItems = itemRepository.findByRentalIdOrderById(id);
        for (RentalItem rentalItem : rentalItems) {
            synchronizeStockBalance(rentalItem.getItem().getId());
        }

        rental.setStatus(DELIVERED);
        rental.setActualReturnDate(now);
        rental.setPaid(true);
        rental.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        Rental savedRental = repository.save(rental);
        registerHistory(savedRental, RENTED, DELIVERED, "Unidades entregues ao cliente.");
        return mapper.toDTO(savedRental);
    }

    @Override
    @Transactional
    public RentalDTO cancel(Long id) {
        findEntity(id);
        throw new IllegalArgumentException("O cancelamento de locação não está disponível.");
    }

    @Override
    @Transactional(readOnly = true)
    public ItemAvailabilityDTO findAvailability(Long itemId) {
        Item item = findActiveItem(itemId);
        long availableQuantity = itemUnitRepository.countByItemIdAndStatusAndActiveTrue(itemId, AVAILABLE);
        ItemAvailabilityDTO dto = new ItemAvailabilityDTO();
        dto.setItemId(item.getId());
        dto.setItemName(item.getName());
        dto.setAvailableQuantity(availableQuantity);
        dto.setReservedQuantity(itemUnitRepository.countByItemIdAndStatusAndActiveTrue(itemId, RESERVED));
        dto.setRentedQuantity(itemUnitRepository.countByItemIdAndStatusAndActiveTrue(itemId, RENTED));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemUnitDTO> findAvailableUnits(Long itemId) {
        findActiveItem(itemId);
        List<ItemUnit> units = itemUnitRepository.findByItemIdAndStatusAndActiveTrueOrderByAssetCode(itemId, AVAILABLE);
        List<ItemUnitDTO> result = new ArrayList<>();
        for (ItemUnit unit : units) {
            result.add(toItemUnitDTO(unit));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemUnitDTO> findItemUnits(Long itemId) {
        findActiveItem(itemId);
        List<ItemUnit> units = itemUnitRepository.findByItemIdOrderByAssetCode(itemId);
        List<ItemUnitDTO> result = new ArrayList<>();
        for (ItemUnit unit : units) {
            result.add(toItemUnitDTO(unit));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalItemUnitDTO> findRentalUnits(Long rentalId) {
        findEntity(rentalId);
        List<RentalItemUnit> units = rentalItemUnitRepository.findByRentalItemRentalIdOrderById(rentalId);
        List<RentalItemUnitDTO> result = new ArrayList<>();
        for (RentalItemUnit unit : units) {
            result.add(toRentalItemUnitDTO(unit));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalStatusHistoryDTO> findHistory(Long rentalId) {
        findEntity(rentalId);
        List<RentalStatusHistory> history = statusHistoryRepository.findByRentalIdOrderByChangedAtAsc(rentalId);
        List<RentalStatusHistoryDTO> result = new ArrayList<>();
        for (RentalStatusHistory entry : history) {
            RentalStatusHistoryDTO dto = new RentalStatusHistoryDTO();
            dto.setId(entry.getId());
            dto.setPreviousStatus(entry.getPreviousStatus());
            dto.setNewStatus(entry.getNewStatus());
            dto.setReason(entry.getReason());
            dto.setChangedAt(entry.getChangedAt());
            dto.setChangedBy(entry.getChangedBy());
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Rental rental = findEntity(id);
        requireDraft(rental);
        itemRepository.deleteByRentalId(id);
        statusHistoryRepository.deleteByRentalId(id);
        repository.delete(rental);
    }

    private void fillRental(Rental rental, RentalSaveDTO dto, boolean newRental) {
        Customer customer;
        if (newRental) {
            customer = findAuthenticatedCustomer();
        } else {
            Optional<Customer> customerOptional = customerRepository.findById(dto.getCustomerId());
            if (!customerOptional.isPresent()) {
                throw new ResourceNotFoundException("Cliente não encontrado.");
            }
            customer = customerOptional.get();
        }
        RentalType rentalType = rentalTypeRepository.findById(dto.getRentalTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de locação não encontrado."));
        if (!Boolean.TRUE.equals(rentalType.getActive())) {
            throw new IllegalArgumentException("O tipo de locação deve estar ativo.");
        }
        if (dto.getExpectedReturnDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("A devolução prevista deve ser posterior à data inicial.");
        }

        rental.setCustomer(customer);
        rental.setRentalType(rentalType);
        rental.setPaymentMethod(null);
        rental.setStartDate(dto.getStartDate());
        rental.setExpectedReturnDate(dto.getExpectedReturnDate());
        rental.setShippingFee(money(dto.getShippingFee()));
        rental.setAdditionalFee(money(dto.getAdditionalFee()));
        rental.setDownPayment(money(dto.getDownPayment()));
        rental.setDeliveryAddress(dto.getDeliveryAddress());
        rental.setNotes(dto.getNotes());
        rental.setLateFee(ZERO);
        rental.setDamageFee(ZERO);
        rental.setContractGenerated(false);
        rental.setWhatsappSent(false);
        rental.setActive(true);

        BigDecimal subtotal = calculateItems(dto.getItems(), rentalType.getDays());
        rental.setDiscount(ZERO);
        BigDecimal total = subtotal.subtract(rental.getDiscount());
        total = total.add(rental.getShippingFee());
        total = total.add(rental.getAdditionalFee());
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O total da locação não pode ser negativo.");
        }
        rental.setSubtotal(subtotal);
        rental.setTotalAmount(total);
        BigDecimal remainingAmount = total.subtract(rental.getDownPayment());
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainingAmount = BigDecimal.ZERO;
        }
        rental.setRemainingAmount(remainingAmount);
    }

    private BigDecimal calculateItems(List<RentalItemSaveDTO> items, Integer rentalDays) {
        BigDecimal total = ZERO;
        Set<Long> ids = new HashSet<>();
        if (rentalDays == null || rentalDays <= 0) {
            throw new IllegalArgumentException("A quantidade de dias da locação deve ser maior que zero.");
        }
        if (items == null) {
            return total;
        }
        for (RentalItemSaveDTO dto : items) {
            if (ids.contains(dto.getItemId())) {
                throw new IllegalArgumentException("Um item não pode aparecer duas vezes.");
            }
            ids.add(dto.getItemId());
            if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            }
            Item item = findActiveItem(dto.getItemId());
            BigDecimal price = item.getPrice();
            if (dto.getUnitPrice() != null) {
                price = money(dto.getUnitPrice());
            }
            validatePricePermission(item, price);
            BigDecimal quantity = BigDecimal.valueOf(dto.getQuantity());
            BigDecimal days = BigDecimal.valueOf(rentalDays);
            BigDecimal subtotal = price.multiply(quantity).multiply(days);
            subtotal = subtotal.subtract(money(dto.getDiscount()));
            subtotal = subtotal.add(money(dto.getAdditionalFee()));
            if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("O subtotal do item não pode ser negativo.");
            }
            total = total.add(subtotal);
        }
        return money(total);
    }

    private List<RentalItem> saveItems(Rental rental, List<RentalItemSaveDTO> dtos) {
        List<RentalItem> savedItems = new ArrayList<>();
        if (dtos == null) {
            return savedItems;
        }
        for (RentalItemSaveDTO dto : dtos) {
            Item item = findActiveItem(dto.getItemId());
            RentalItem entity = new RentalItem();
            entity.setRental(rental);
            entity.setItem(item);
            entity.setQuantity(dto.getQuantity());
            BigDecimal unitPrice;
            if (dto.getUnitPrice() == null) {
                unitPrice = money(item.getPrice());
            } else {
                unitPrice = money(dto.getUnitPrice());
            }
            validatePricePermission(item, unitPrice);
            entity.setUnitPrice(unitPrice);
            entity.setDiscount(money(dto.getDiscount()));
            entity.setAdditionalFee(money(dto.getAdditionalFee()));
            BigDecimal quantity = BigDecimal.valueOf(entity.getQuantity());
            BigDecimal days = BigDecimal.valueOf(rental.getRentalType().getDays());
            BigDecimal subtotal = entity.getUnitPrice().multiply(quantity).multiply(days);
            subtotal = subtotal.subtract(entity.getDiscount());
            subtotal = subtotal.add(entity.getAdditionalFee());
            entity.setSubtotal(subtotal);
            itemRepository.save(entity);
            savedItems.add(entity);
        }
        return savedItems;
    }

    private void reserveUnits(RentalItem rentalItem) {
        int requestedQuantity = rentalItem.getQuantity();
        List<ItemUnit> availableUnits = itemUnitRepository.findAvailableForReservation(
                rentalItem.getItem().getId(), PageRequest.of(0, requestedQuantity));

        if (availableUnits.size() < requestedQuantity) {
            throw new IllegalArgumentException("Quantidade indisponível para o item "
                    + rentalItem.getItem().getName() + ". Disponíveis: " + availableUnits.size()
                    + ", solicitadas: " + requestedQuantity + ".");
        }

        String username = authenticationFacade.getAuthenticatedUsername();
        Instant now = Instant.now();
        for (ItemUnit itemUnit : availableUnits) {
            if (!itemUnit.getItem().getId().equals(rentalItem.getItem().getId())) {
                throw new IllegalArgumentException("A unidade selecionada não pertence ao item da locação.");
            }
            if (!AVAILABLE.equals(itemUnit.getStatus()) || !Boolean.TRUE.equals(itemUnit.getActive())) {
                throw new IllegalArgumentException("A unidade " + itemUnit.getAssetCode() + " não está disponível.");
            }

            List<RentalItemUnitStatus> activeStatuses = Arrays.asList(
                    RentalItemUnitStatus.RESERVED, RentalItemUnitStatus.DELIVERED);
            boolean unitAlreadyUsed = rentalItemUnitRepository.existsByItemUnitIdAndStatusIn(
                    itemUnit.getId(), activeStatuses);
            if (unitAlreadyUsed) {
                throw new IllegalArgumentException("A unidade " + itemUnit.getAssetCode()
                        + " já está vinculada a uma locação ativa.");
            }

            ItemUnit savedItemUnit = itemUnit;
            savedItemUnit.setStatus(RESERVED);
            savedItemUnit.setUpdatedBy(username);
            itemUnitRepository.save(savedItemUnit);

            RentalItemUnit rentalItemUnit = new RentalItemUnit();
            rentalItemUnit.setRentalItem(rentalItem);
            rentalItemUnit.setItemUnit(savedItemUnit);
            rentalItemUnit.setStatus(RentalItemUnitStatus.RESERVED);
            rentalItemUnit.setReservedAt(now);
            rentalItemUnit.setCreatedBy(username);
            rentalItemUnitRepository.save(rentalItemUnit);
        }

        List<RentalItemUnitStatus> activeStatuses = Arrays.asList(
                RentalItemUnitStatus.RESERVED, RentalItemUnitStatus.DELIVERED);
        long linkedQuantity = rentalItemUnitRepository.countByRentalItemIdAndStatusIn(
                rentalItem.getId(), activeStatuses);
        if (linkedQuantity != requestedQuantity) {
            throw new IllegalArgumentException("A quantidade de unidades vinculadas não corresponde à quantidade solicitada.");
        }
        synchronizeStockBalance(rentalItem.getItem().getId());
    }

    private void synchronizeStockBalance(Long itemId) {
        Optional<StockBalance> balance = stockBalanceRepository.findByItemIdForUpdate(itemId);
        if (!balance.isPresent()) {
            throw new IllegalArgumentException("O item não possui saldo de estoque cadastrado.");
        }
        StockBalance stockBalance = balance.get();
        int total = (int) itemUnitRepository.countByItemIdAndActiveTrue(itemId);
        int reserved = (int) itemUnitRepository.countByItemIdAndStatusAndActiveTrue(itemId, RESERVED);
        int unavailable = (int) itemUnitRepository.countUnavailableByItemId(itemId);
        stockBalance.setTotalQuantity(total);
        stockBalance.setReservedQuantity(reserved);
        stockBalance.setUnavailableQuantity(unavailable);
        stockBalance.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        stockBalanceRepository.save(stockBalance);
    }

    private void validateReservedUnitQuantity(Long rentalId, List<RentalItemUnit> units) {
        List<RentalItem> rentalItems = itemRepository.findByRentalIdOrderById(rentalId);
        int expectedQuantity = 0;
        for (RentalItem rentalItem : rentalItems) {
            expectedQuantity = expectedQuantity + rentalItem.getQuantity();
        }
        if (units.size() != expectedQuantity) {
            throw new IllegalArgumentException("A quantidade de unidades reservadas não corresponde aos itens da locação.");
        }
    }

    private void registerHistory(Rental rental, String previousStatus, String newStatus, String reason) {
        String username = authenticationFacade.getAuthenticatedUsername();
        RentalStatusHistory history = new RentalStatusHistory();
        history.setRental(rental);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setReason(reason);
        history.setChangedAt(Instant.now());
        history.setChangedBy(username);
        history.setCreatedBy(username);
        statusHistoryRepository.save(history);
    }

    private ItemUnitDTO toItemUnitDTO(ItemUnit unit) {
        ItemUnitDTO dto = new ItemUnitDTO();
        dto.setId(unit.getId());
        dto.setItemId(unit.getItem().getId());
        dto.setItemName(unit.getItem().getName());
        dto.setAssetCode(unit.getAssetCode());
        dto.setSerialNumber(unit.getSerialNumber());
        dto.setStatus(unit.getStatus());
        dto.setConditionStatus(unit.getConditionStatus());
        dto.setActive(unit.getActive());
        return dto;
    }

    private RentalItemUnitDTO toRentalItemUnitDTO(RentalItemUnit unit) {
        RentalItemUnitDTO dto = new RentalItemUnitDTO();
        dto.setId(unit.getId());
        dto.setRentalItemId(unit.getRentalItem().getId());
        dto.setItemUnitId(unit.getItemUnit().getId());
        dto.setItemName(unit.getRentalItem().getItem().getName());
        dto.setAssetCode(unit.getItemUnit().getAssetCode());
        dto.setStatus(unit.getStatus().name());
        dto.setReservedAt(unit.getReservedAt());
        dto.setDeliveredAt(unit.getDeliveredAt());
        dto.setReturnedAt(unit.getReturnedAt());
        return dto;
    }

    private Rental findEntity(Long id) {
        Optional<Rental> rental = repository.findById(id);
        if (!rental.isPresent()) {
            throw new ResourceNotFoundException("Locação não encontrada.");
        }
        return rental.get();
    }
    private Customer findAuthenticatedCustomer() {
        String email = authenticationFacade.getAuthenticatedUsername();
        Customer customer = customerRepository.findByEmail(email);

        if (customer == null) {
            throw new ResourceNotFoundException(
                    "O usuário autenticado não possui um cliente cadastrado com o mesmo e-mail."
            );
        }

        if (!Boolean.TRUE.equals(customer.getActive())) {
            throw new IllegalArgumentException("O cliente do usuário autenticado deve estar ativo.");
        }

        return customer;
    }
    private Item findActiveItem(Long id) {
        Optional<Item> itemOptional = inventoryItemRepository.findById(id);
        if (!itemOptional.isPresent()) {
            throw new ResourceNotFoundException("Item não encontrado.");
        }
        Item item = itemOptional.get();
        if (!Boolean.TRUE.equals(item.getActive())) {
            throw new IllegalArgumentException("Somente itens ativos podem ser adicionados.");
        }
        return item;
    }
    private void requireDraft(Rental rental) {
        throw new IllegalArgumentException("Locações alugadas ou entregues não podem ser alteradas diretamente.");
    }
    private void validatePricePermission(Item item, BigDecimal unitPrice) {
        BigDecimal itemPrice = money(item.getPrice());
        BigDecimal informedPrice = money(unitPrice);
        if (itemPrice.compareTo(informedPrice) == 0) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean allowed = false;
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String authorityName = authority.getAuthority();
                if ("RENTAL_PRICE_CHANGE".equals(authorityName)
                        || "ROLE_ADMINISTRADOR".equals(authorityName)) {
                    allowed = true;
                    break;
                }
            }
        }
        if (!allowed) {
            throw new IllegalArgumentException("Usuário sem permissão para alterar o preço do item.");
        }
    }
    private BigDecimal money(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
    private String text(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
    private String generateNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        formatter = formatter.withZone(ZoneOffset.UTC);
        String date = formatter.format(Instant.now());
        return "LOC-" + date;
    }
}
