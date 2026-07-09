package com.locadora_rdt_backend.modules.inventory.stockbalances.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.infrastructure.security.AuthenticationFacade;
import com.locadora_rdt_backend.modules.inventory.stockbalances.constants.StockBalanceErrorMessages;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceDetailsDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.dto.StockBalanceUpdateDTO;
import com.locadora_rdt_backend.modules.inventory.stockbalances.mapper.StockBalanceMapper;
import com.locadora_rdt_backend.modules.inventory.stockbalances.model.StockBalance;
import com.locadora_rdt_backend.modules.inventory.stockbalances.repository.StockBalanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockBalanceServiceImpl implements StockBalanceService {

    private final StockBalanceRepository repository;
    private final StockBalanceMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public StockBalanceServiceImpl(
            StockBalanceRepository repository,
            StockBalanceMapper mapper,
            AuthenticationFacade authenticationFacade
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockBalanceDTO> findAllPaged(String name, PageRequest pageRequest) {
        return repository.find(normalizeName(name), pageRequest)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StockBalanceDetailsDTO findById(Long id) {
        StockBalance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockBalanceErrorMessages.STOCK_BALANCE_NOT_FOUND
                ));

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public StockBalanceDetailsDTO findByItemId(Long itemId) {
        StockBalance entity = repository.findByItemId(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockBalanceErrorMessages.STOCK_BALANCE_NOT_FOUND
                ));

        return mapper.toDetailsDTO(entity);
    }

    @Override
    @Transactional
    public StockBalanceDTO update(Long id, StockBalanceUpdateDTO dto) {
        validateStockBalance(dto);

        StockBalance entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        StockBalanceErrorMessages.STOCK_BALANCE_NOT_FOUND
                ));

        mapper.copyToEntity(dto, entity);
        entity.setUpdatedBy(authenticationFacade.getAuthenticatedUsername());

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private void validateStockBalance(StockBalanceUpdateDTO dto) {
        if (dto.getTotalQuantity() < dto.getReservedQuantity() + dto.getUnavailableQuantity()) {
            throw new IllegalArgumentException(
                    "A quantidade total não pode ser menor que reservado mais indisponível."
            );
        }
    }
}
