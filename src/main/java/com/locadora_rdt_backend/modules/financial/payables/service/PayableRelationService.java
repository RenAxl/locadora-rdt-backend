package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.common.exception.ResourceNotFoundException;
import com.locadora_rdt_backend.modules.financial.payables.constants.PayableErrorMessages;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableSaveDTO;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.financial.payment_frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment_frequencies.repository.PaymentFrequencyRepository;
import com.locadora_rdt_backend.modules.financial.payment_methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payment_methods.repository.PaymentMethodRepository;
import com.locadora_rdt_backend.modules.organization.employees.model.Employee;
import com.locadora_rdt_backend.modules.organization.employees.repository.EmployeeRepository;
import com.locadora_rdt_backend.modules.organization.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.organization.suppliers.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PayableRelationService {

    private final SupplierRepository supplierRepository;
    private final EmployeeRepository employeeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentFrequencyRepository paymentFrequencyRepository;

    public PayableRelationService(
            SupplierRepository supplierRepository,
            EmployeeRepository employeeRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentFrequencyRepository paymentFrequencyRepository
    ) {
        this.supplierRepository = supplierRepository;
        this.employeeRepository = employeeRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentFrequencyRepository = paymentFrequencyRepository;
    }

    public void applyRelations(Payable entity, PayableSaveDTO dto) {
        entity.setSupplier(findSupplier(normalizeId(dto.getSupplierId())));
        entity.setEmployee(findEmployee(normalizeId(dto.getEmployeeId())));
        entity.setPaymentMethod(findPaymentMethod(normalizeId(dto.getPaymentMethodId())));
        entity.setPaymentFrequency(findPaymentFrequency(normalizeId(dto.getPaymentFrequencyId())));
    }

    public PaymentMethod findPaymentMethod(Long id) {
        Long normalizedId = normalizeId(id);
        if (normalizedId == null) {
            return null;
        }

        Optional<PaymentMethod> optionalPaymentMethod = paymentMethodRepository.findById(normalizedId);

        if (optionalPaymentMethod.isEmpty()) {
            throw new ResourceNotFoundException(PayableErrorMessages.PAYMENT_METHOD_NOT_FOUND + normalizedId);
        }

        return optionalPaymentMethod.get();
    }

    private Supplier findSupplier(Long id) {
        if (id == null) {
            return null;
        }

        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);

        if (optionalSupplier.isEmpty()) {
            throw new ResourceNotFoundException(PayableErrorMessages.SUPPLIER_NOT_FOUND + id);
        }

        return optionalSupplier.get();
    }

    private Employee findEmployee(Long id) {
        if (id == null) {
            return null;
        }

        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (optionalEmployee.isEmpty()) {
            throw new ResourceNotFoundException(PayableErrorMessages.EMPLOYEE_NOT_FOUND + id);
        }

        return optionalEmployee.get();
    }

    private PaymentFrequency findPaymentFrequency(Long id) {
        if (id == null) {
            return null;
        }

        Optional<PaymentFrequency> optionalPaymentFrequency = paymentFrequencyRepository.findById(id);

        if (optionalPaymentFrequency.isEmpty()) {
            throw new ResourceNotFoundException(PayableErrorMessages.PAYMENT_FREQUENCY_NOT_FOUND + id);
        }

        return optionalPaymentFrequency.get();
    }

    private Long normalizeId(Long id) {
        if (id == null || id <= 0) {
            return null;
        }

        return id;
    }
}
