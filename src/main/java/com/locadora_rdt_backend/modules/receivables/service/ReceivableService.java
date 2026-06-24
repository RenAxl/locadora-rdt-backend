package com.locadora_rdt_backend.modules.receivables.service;

import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableInsertDTO;

public interface ReceivableService {

    ReceivableDTO insert(ReceivableInsertDTO dto);
}
