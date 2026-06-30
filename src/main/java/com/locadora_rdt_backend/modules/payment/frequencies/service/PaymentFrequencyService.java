package com.locadora_rdt_backend.modules.payment.frequencies.service;

import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyDetailsDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyInsertDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.dto.PaymentFrequencyUpdateDTO;
import com.locadora_rdt_backend.modules.payment.frequencies.model.PaymentFrequency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface PaymentFrequencyService {

    Page<PaymentFrequencyDTO> findAllPaged(String frequency, PageRequest pageRequest);

    PaymentFrequencyDetailsDTO findById(Long id);

    PaymentFrequencyDTO insert(PaymentFrequencyInsertDTO dto);

    PaymentFrequencyDTO update(Long id, PaymentFrequencyUpdateDTO dto);

    PaymentFrequency findEntityById(Long id);

    void delete(Long id);

    void deleteAll(List<Long> ids);
}
