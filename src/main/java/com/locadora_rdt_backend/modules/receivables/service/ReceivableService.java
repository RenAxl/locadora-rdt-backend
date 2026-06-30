package com.locadora_rdt_backend.modules.receivables.service;

import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ReceivableService {

    Page<ReceivableDTO> findAllPaged(String description, PageRequest pageRequest);
}
