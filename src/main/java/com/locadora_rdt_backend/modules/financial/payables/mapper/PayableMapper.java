package com.locadora_rdt_backend.modules.financial.payables.mapper;

import com.locadora_rdt_backend.modules.suppliers.model.Supplier;
import com.locadora_rdt_backend.modules.employees.model.Employee;
import com.locadora_rdt_backend.modules.financial.payment.frequencies.model.PaymentFrequency;
import com.locadora_rdt_backend.modules.financial.payment.methods.model.PaymentMethod;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableDetailsDTO;
import com.locadora_rdt_backend.modules.financial.payables.dto.PayableSaveDTO;
import com.locadora_rdt_backend.modules.financial.payables.model.Payable;
import com.locadora_rdt_backend.modules.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class PayableMapper {

    public PayableDTO toDTO(Payable entity) {
        PayableDTO dto = new PayableDTO();
        copyToDTO(entity, dto);
        return dto;
    }

    public PayableDetailsDTO toDetailsDTO(Payable entity) {
        PayableDetailsDTO dto = new PayableDetailsDTO();
        copyToDTO(entity, dto);
        return dto;
    }

    private void copyToDTO(Payable entity, PayableDTO dto) {

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
        dto.setParentPayableId(entity.getParentPayable() == null ? null : entity.getParentPayable().getId());

        copySupplier(entity.getSupplier(), dto);
        copyEmployee(entity.getEmployee(), dto);
        copyPaymentMethod(entity.getPaymentMethod(), dto);
        copyPaymentFrequency(entity.getPaymentFrequency(), dto);
        copyCreatedBy(entity.getCreatedBy(), dto);
        copyPaidBy(entity.getPaidBy(), dto);

    }

    public Payable toEntity(PayableSaveDTO dto) {
        Payable entity = new Payable();
        updateEntity(entity, dto);
        return entity;
    }

    public void updateEntity(Payable entity, PayableSaveDTO dto) {
        entity.setDescription(trimToNull(dto.getDescription()));
        entity.setAmount(dto.getAmount());
        entity.setDueDate(dto.getDueDate());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setNote(trimToNull(dto.getNote()));
        entity.setFileName(trimToNull(dto.getFileName()));
        entity.setPaid(dto.getPaymentDate() != null);
        entity.setRemainingBalance(dto.getPaymentDate() == null ? dto.getAmount() : java.math.BigDecimal.ZERO);
    }

    private void copySupplier(Supplier supplier, PayableDTO dto) {
        if (supplier == null) {
            return;
        }

        dto.setSupplierId(supplier.getId());
        dto.setSupplierName(supplier.getName());
    }

    private void copyEmployee(Employee employee, PayableDTO dto) {
        if (employee == null) {
            return;
        }

        dto.setEmployeeId(employee.getId());
        dto.setEmployeeName(employee.getName());
    }

    private void copyPaymentMethod(PaymentMethod paymentMethod, PayableDTO dto) {
        if (paymentMethod == null) {
            return;
        }

        dto.setPaymentMethodId(paymentMethod.getId());
        dto.setPaymentMethodName(paymentMethod.getName());
    }

    private void copyPaymentFrequency(PaymentFrequency paymentFrequency, PayableDTO dto) {
        if (paymentFrequency == null) {
            return;
        }

        dto.setPaymentFrequencyId(paymentFrequency.getId());
        dto.setPaymentFrequency(paymentFrequency.getFrequency());
    }

    private void copyCreatedBy(User createdBy, PayableDTO dto) {
        if (createdBy == null) {
            return;
        }

        dto.setCreatedById(createdBy.getId());
        dto.setCreatedByName(createdBy.getName());
    }

    private void copyPaidBy(User paidBy, PayableDTO dto) {
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
