package com.locadora_rdt_backend.modules.rental.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.customers.dto.CustomerDTO;
import com.locadora_rdt_backend.modules.customers.repository.CustomerRepository;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment.methods.repository.PaymentMethodRepository;
import com.locadora_rdt_backend.modules.inventory.items.model.Item;
import com.locadora_rdt_backend.modules.inventory.items.repository.ItemRepository;
import com.locadora_rdt_backend.modules.rental.dto.*;
import com.locadora_rdt_backend.modules.rental.mapper.RentalMapper;
import com.locadora_rdt_backend.modules.rental.model.Rental;
import com.locadora_rdt_backend.modules.rental.model.RentalItem;
import com.locadora_rdt_backend.modules.rental.repository.RentalItemRepository;
import com.locadora_rdt_backend.modules.rental.repository.RentalRepository;
import com.locadora_rdt_backend.modules.rentaltypes.model.RentalType;
import com.locadora_rdt_backend.modules.rentaltypes.repository.RentalTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RentalServiceImpl implements RentalService {
    private static final String DRAFT = "DRAFT";
    private static final String CONFIRMED = "CONFIRMED";
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final Instant FIRST_DATE = Instant.parse("1900-01-01T00:00:00Z");
    private static final Instant LAST_DATE = Instant.parse("2999-12-31T23:59:59Z");

    private final RentalRepository repository;
    private final RentalItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final RentalTypeRepository rentalTypeRepository;
    private final ItemRepository inventoryItemRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final RentalMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public RentalServiceImpl(RentalRepository repository, RentalItemRepository itemRepository,
            CustomerRepository customerRepository, RentalTypeRepository rentalTypeRepository,
            ItemRepository inventoryItemRepository, PaymentMethodRepository paymentMethodRepository,
            RentalMapper mapper, AuthenticationFacade authenticationFacade) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
        this.rentalTypeRepository = rentalTypeRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalDTO> findAllPaged(String number, String customer, String status, Long rentalTypeId,
            Instant dateFrom, Instant dateTo, PageRequest pageRequest) {
        Long typeId = rentalTypeId == null ? -1L : rentalTypeId;
        Instant initialDate = dateFrom == null ? FIRST_DATE : dateFrom;
        Instant finalDate = dateTo == null ? LAST_DATE : dateTo;

        return repository.findFiltered(text(number), text(customer), text(status), typeId,
                initialDate, finalDate, pageRequest).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalDetailsDTO findById(Long id) {
        Rental rental = findEntity(id);
        return mapper.toDetailsDTO(rental, itemRepository.findByRentalIdOrderById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO findCurrentCustomer() {
        Customer customer = findAuthenticatedCustomer();
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setCpf(customer.getCpf());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setActive(customer.getActive());
        return dto;
    }

    @Override
    @Transactional
    public RentalDTO insert(RentalSaveDTO dto) {
        Rental rental = new Rental();
        rental.setRentalNumber(generateNumber());
        rental.setStatus(DRAFT);
        rental.setRentalDate(Instant.now());
        rental.setCreatedBy(authenticationFacade.getAuthenticatedUsername());
        fillRental(rental, dto, true);
        rental = repository.save(rental);
        saveItems(rental, dto.getItems());
        return mapper.toDTO(rental);
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
        return mapper.toDTO(repository.save(rental));
    }

    @Override
    @Transactional
    public RentalDTO confirm(Long id) {
        Rental rental = findEntity(id);
        requireDraft(rental);
        if (rental.getCustomer() == null) throw new IllegalArgumentException("Cliente é obrigatório.");
        if (!Boolean.TRUE.equals(rental.getCustomer().getActive())) throw new IllegalArgumentException("O cliente deve estar ativo.");
        if (itemRepository.findByRentalIdOrderById(id).isEmpty()) throw new IllegalArgumentException("Adicione pelo menos um item.");
        rental.setStatus(CONFIRMED);
        rental.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());
        return mapper.toDTO(repository.save(rental));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Rental rental = findEntity(id);
        requireDraft(rental);
        itemRepository.deleteByRentalId(id);
        repository.delete(rental);
    }

    private void fillRental(Rental rental, RentalSaveDTO dto, boolean newRental) {
        Customer customer = newRental
                ? findAuthenticatedCustomer()
                : customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
        RentalType rentalType = rentalTypeRepository.findById(dto.getRentalTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de locação não encontrado."));
        if (!Boolean.TRUE.equals(rentalType.getActive())) throw new IllegalArgumentException("O tipo de locação deve estar ativo.");
        if (dto.getExpectedReturnDate().isBefore(dto.getStartDate())) throw new IllegalArgumentException("A devolução prevista deve ser posterior à data inicial.");

        rental.setCustomer(customer);
        rental.setRentalType(rentalType);
        rental.setPaymentMethod(findPaymentMethod(dto.getPaymentMethodId()));
        rental.setStartDate(dto.getStartDate());
        rental.setExpectedReturnDate(dto.getExpectedReturnDate());
        rental.setDiscount(money(dto.getDiscount()));
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

        BigDecimal subtotal = calculateItems(dto.getItems());
        BigDecimal total = subtotal.subtract(rental.getDiscount()).add(rental.getShippingFee()).add(rental.getAdditionalFee());
        if (total.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("O total da locação não pode ser negativo.");
        rental.setSubtotal(subtotal);
        rental.setTotalAmount(total);
        rental.setRemainingAmount(total.subtract(rental.getDownPayment()).max(BigDecimal.ZERO));
    }

    private BigDecimal calculateItems(List<RentalItemSaveDTO> items) {
        BigDecimal total = ZERO;
        Set<Long> ids = new HashSet<>();
        if (items == null) return total;
        for (RentalItemSaveDTO dto : items) {
            if (!ids.add(dto.getItemId())) throw new IllegalArgumentException("Um item não pode aparecer duas vezes.");
            if (dto.getQuantity() == null || dto.getQuantity() <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            Item item = findActiveItem(dto.getItemId());
            BigDecimal price = dto.getUnitPrice() == null ? item.getPrice() : money(dto.getUnitPrice());
            validatePricePermission(item, price);
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(dto.getQuantity()))
                    .subtract(money(dto.getDiscount())).add(money(dto.getAdditionalFee()));
            if (subtotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("O subtotal do item não pode ser negativo.");
            total = total.add(subtotal);
        }
        return money(total);
    }

    private void saveItems(Rental rental, List<RentalItemSaveDTO> dtos) {
        if (dtos == null) return;
        for (RentalItemSaveDTO dto : dtos) {
            Item item = findActiveItem(dto.getItemId());
            RentalItem entity = new RentalItem();
            entity.setRental(rental);
            entity.setItem(item);
            entity.setQuantity(dto.getQuantity());
            BigDecimal unitPrice = dto.getUnitPrice() == null ? money(item.getPrice()) : money(dto.getUnitPrice());
            validatePricePermission(item, unitPrice);
            entity.setUnitPrice(unitPrice);
            entity.setDiscount(money(dto.getDiscount()));
            entity.setAdditionalFee(money(dto.getAdditionalFee()));
            entity.setSubtotal(entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity()))
                    .subtract(entity.getDiscount()).add(entity.getAdditionalFee()));
            itemRepository.save(entity);
        }
    }

    private Rental findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Locação não encontrada."));
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
        Item item = inventoryItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item não encontrado."));
        if (!Boolean.TRUE.equals(item.getActive())) throw new IllegalArgumentException("Somente itens ativos podem ser adicionados.");
        return item;
    }
    private PaymentMethod findPaymentMethod(Long id) {
        if (id == null) return null;
        return paymentMethodRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Forma de pagamento não encontrada."));
    }
    private void requireDraft(Rental rental) {
        if (!DRAFT.equals(rental.getStatus())) throw new IllegalArgumentException("Somente locações em rascunho podem ser alteradas ou excluídas.");
    }
    private void validatePricePermission(Item item, BigDecimal unitPrice) {
        if (money(item.getPrice()).compareTo(money(unitPrice)) == 0) return;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean allowed = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "RENTAL_PRICE_CHANGE".equals(authority.getAuthority())
                        || "ROLE_ADMINISTRADOR".equals(authority.getAuthority()));
        if (!allowed) throw new IllegalArgumentException("Usuário sem permissão para alterar o preço do item.");
    }
    private BigDecimal money(BigDecimal value) { return (value == null ? ZERO : value).setScale(2, RoundingMode.HALF_UP); }
    private String text(String value) { return value == null ? "" : value.trim(); }
    private String generateNumber() {
        return "LOC-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").withZone(ZoneOffset.UTC).format(Instant.now());
    }
}
