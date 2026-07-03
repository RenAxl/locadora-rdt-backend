package com.locadora_rdt_backend.modules.financial.payment.methods.service;

import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodInsertDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.dto.PaymentMethodUpdateDTO;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface PaymentMethodService {

    Page<PaymentMethodDTO> findAllPaged(String name, PageRequest pageRequest);

    PaymentMethodDetailsDTO findById(Long id);

    PaymentMethodDTO insert(PaymentMethodInsertDTO dto);

    PaymentMethodDTO update(Long id, PaymentMethodUpdateDTO dto);

    PaymentMethod findEntityById(Long id);

    void delete(Long id);

    void deleteAll(List<Long> ids);
}
