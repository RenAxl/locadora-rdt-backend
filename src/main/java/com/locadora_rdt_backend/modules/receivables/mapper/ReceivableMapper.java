package com.locadora_rdt_backend.modules.receivables.mapper;

import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class ReceivableMapper {

    public ReceivableDTO toDTO(Receivable entity) {
        ReceivableDTO dto = new ReceivableDTO();

        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setFileName(entity.getFileName());
        dto.setPaid(entity.getPaid());
        dto.setRemainingBalance(entity.getRemainingBalance());

        copyCustomer(entity.getCustomer(), dto);
        copyPaymentMethod(entity.getPaymentMethod(), dto);
        copyPaymentFrequency(entity.getPaymentFrequency(), dto);
        copyCreatedBy(entity.getCreatedBy(), dto);
        copyPaidBy(entity.getPaidBy(), dto);

        return dto;
    }

    private void copyCustomer(Customer customer, ReceivableDTO dto) {
        if (customer == null) {
            return;
        }

        dto.setCustomerId(customer.getId());
        dto.setCustomerName(customer.getName());
    }

    private void copyPaymentMethod(PaymentMethod paymentMethod, ReceivableDTO dto) {
        if (paymentMethod == null) {
            return;
        }

        dto.setPaymentMethodId(paymentMethod.getId());
        dto.setPaymentMethodName(paymentMethod.getName());
    }

    private void copyPaymentFrequency(PaymentFrequency paymentFrequency, ReceivableDTO dto) {
        if (paymentFrequency == null) {
            return;
        }

        dto.setPaymentFrequencyId(paymentFrequency.getId());
        dto.setPaymentFrequency(paymentFrequency.getFrequency());
    }

    private void copyCreatedBy(User createdBy, ReceivableDTO dto) {
        if (createdBy == null) {
            return;
        }

        dto.setCreatedById(createdBy.getId());
        dto.setCreatedByName(createdBy.getName());
    }

    private void copyPaidBy(User paidBy, ReceivableDTO dto) {
        if (paidBy == null) {
            return;
        }

        dto.setPaidById(paidBy.getId());
        dto.setPaidByName(paidBy.getName());
    }
}
