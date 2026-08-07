package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants;
import com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableErrorMessages;
import com.locadora_rdt_backend.modules.financial.payment_frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment_frequencies.repository.PaymentFrequencyRepository;
import com.locadora_rdt_backend.modules.financial.payment_methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment_methods.repository.PaymentMethodRepository;
import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableSaveDTO;
import com.locadora_rdt_backend.modules.financial.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.organization.customers.model.Customer;
import com.locadora_rdt_backend.modules.organization.customers.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReceivableRelationService {

    private final CustomerRepository customerRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentFrequencyRepository paymentFrequencyRepository;

    public ReceivableRelationService(
            CustomerRepository customerRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentFrequencyRepository paymentFrequencyRepository
    ) {
        this.customerRepository = customerRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentFrequencyRepository = paymentFrequencyRepository;
    }

    public void applyRelations(Receivable entity, ReceivableSaveDTO dto) {
        entity.setCustomer(findCustomer(normalizeId(dto.getCustomerId())));
        entity.setPaymentMethod(findPaymentMethod(dto.getPaymentMethodId()));
        entity.setPaymentFrequency(findPaymentFrequency(normalizeId(dto.getPaymentFrequencyId())));
    }

    public PaymentMethod findPaymentMethod(Long id) {
        Long normalizedId = normalizeId(id);
        if (normalizedId == null) {
            return null;
        }

        Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findById(normalizedId);
        if (paymentMethod.isEmpty()) {
            throw new ResourceNotFoundException(ReceivableErrorMessages.PAYMENT_METHOD_NOT_FOUND + normalizedId);
        }

        return paymentMethod.get();
    }

    public PaymentFrequency findCashPaymentFrequency() {
        return paymentFrequencyRepository.findByFrequencyIgnoreCase(ReceivableConstants.CASH_PAYMENT_FREQUENCY)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ReceivableErrorMessages.CASH_PAYMENT_FREQUENCY_NOT_FOUND
                ));
    }

    public Long normalizeId(Long id) {
        if (id == null || id <= 0) {
            return null;
        }

        return id;
    }

    private Customer findCustomer(Long id) {
        if (id == null) {
            return null;
        }

        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            throw new ResourceNotFoundException(ReceivableErrorMessages.CUSTOMER_NOT_FOUND + id);
        }

        return customer.get();
    }

    private PaymentFrequency findPaymentFrequency(Long id) {
        if (id == null) {
            return null;
        }

        Optional<PaymentFrequency> paymentFrequency = paymentFrequencyRepository.findById(id);
        if (paymentFrequency.isEmpty()) {
            throw new ResourceNotFoundException(ReceivableErrorMessages.PAYMENT_FREQUENCY_NOT_FOUND + id);
        }

        return paymentFrequency.get();
    }
}
