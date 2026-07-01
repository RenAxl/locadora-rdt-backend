package com.locadora_rdt_backend.modules.receivables.mapper;

import com.locadora_rdt_backend.modules.customers.model.Customer;
import com.locadora_rdt_backend.modules.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableDetailsDTO;
import com.locadora_rdt_backend.modules.receivables.dto.ReceivableSaveDTO;
import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import com.locadora_rdt_backend.modules.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class ReceivableMapper {

    public ReceivableDTO toDTO(Receivable entity) {
        ReceivableDTO dto = new ReceivableDTO();
        copyToDTO(entity, dto);
        return dto;
    }

    public ReceivableDetailsDTO toDetailsDTO(Receivable entity) {
        ReceivableDetailsDTO dto = new ReceivableDetailsDTO();
        copyToDTO(entity, dto);
        return dto;
    }

    private void copyToDTO(Receivable entity, ReceivableDTO dto) {

        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setNote(entity.getNote());
        dto.setFileName(entity.getFileName());
        dto.setPaid(entity.getPaid());
        dto.setRemainingBalance(entity.getRemainingBalance());
        dto.setLateFee(entity.getLateFee());
        dto.setLateInterest(entity.getLateInterest());
        dto.setDiscount(entity.getDiscount());
        dto.setFee(entity.getFee());
        dto.setSubtotal(entity.getSubtotal());
        dto.setResidual(entity.getResidual());
        dto.setCanceled(entity.getCanceled());
        dto.setParentReceivableId(entity.getParentReceivable() == null ? null : entity.getParentReceivable().getId());

        copyCustomer(entity.getCustomer(), dto);
        copyPaymentMethod(entity.getPaymentMethod(), dto);
        copyPaymentFrequency(entity.getPaymentFrequency(), dto);
        copyCreatedBy(entity.getCreatedBy(), dto);
        copyPaidBy(entity.getPaidBy(), dto);

    }

    public Receivable toEntity(ReceivableSaveDTO dto) {
        Receivable entity = new Receivable();
        updateEntity(entity, dto);
        return entity;
    }

    public void updateEntity(Receivable entity, ReceivableSaveDTO dto) {
        entity.setDescription(trimToNull(dto.getDescription()));
        entity.setAmount(dto.getAmount());
        entity.setDueDate(dto.getDueDate());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setNote(trimToNull(dto.getNote()));
        entity.setFileName(trimToNull(dto.getFileName()));
        entity.setPaid(dto.getPaymentDate() != null);
        entity.setRemainingBalance(dto.getPaymentDate() == null ? dto.getAmount() : java.math.BigDecimal.ZERO);
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

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}
