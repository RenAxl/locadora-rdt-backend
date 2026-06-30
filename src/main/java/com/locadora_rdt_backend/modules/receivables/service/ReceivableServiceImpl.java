package com.locadora_rdt_backend.modules.receivables.service;

import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.mapper.ReceivableMapper;
import com.locadora_rdt_backend.modules.receivables.repository.ReceivableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceivableServiceImpl implements ReceivableService {

    private final ReceivableRepository repository;
    private final ReceivableMapper mapper;

    public ReceivableServiceImpl(
            ReceivableRepository repository,
            ReceivableMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReceivableDTO> findAllPaged(String description, PageRequest pageRequest) {
        return repository.find(description, pageRequest)
                .map(mapper::toDTO);
    }
}
