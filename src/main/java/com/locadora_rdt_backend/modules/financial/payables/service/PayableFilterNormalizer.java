package com.locadora_rdt_backend.modules.financial.payables.service;

import com.locadora_rdt_backend.modules.financial.payables.dto.PayableFilterDTO;
import com.locadora_rdt_backend.modules.financial.payables.model.PayableStatus;
import org.springframework.stereotype.Component;

@Component
public class PayableFilterNormalizer {

    public PayableFilterDTO normalize(PayableFilterDTO filters) {
        PayableFilterDTO source;

        if (filters == null) {
            source = new PayableFilterDTO();
        } else {
            source = filters;
        }

        PayableFilterDTO normalized = new PayableFilterDTO();

        normalized.setSearch(trimToNull(source.getSearch()));
        normalized.setStartDate(source.getStartDate());
        normalized.setEndDate(source.getEndDate());
        normalized.setStatus(normalizeStatus(source.getStatus()));
        normalized.setPeriodType(normalizePeriodType(source.getPeriodType()));
        normalized.setSupplierId(normalizeId(source.getSupplierId()));
        normalized.setEmployeeId(normalizeId(source.getEmployeeId()));
        normalized.setPaymentMethodId(normalizeId(source.getPaymentMethodId()));
        normalized.setPaymentFrequencyId(normalizeId(source.getPaymentFrequencyId()));
        normalized.setMinimumAmount(source.getMinimumAmount());
        normalized.setMaximumAmount(source.getMaximumAmount());
        normalized.setOrderBy(normalizeOrderBy(source.getOrderBy()));
        normalized.setDirection(normalizeDirection(source.getDirection()));

        return normalized;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return PayableStatus.ALL.name();
        }

        String value = status.trim().toUpperCase();

        if ("OPEN".equals(value)) {
            return PayableStatus.PENDING.name();
        }

        if (PayableStatus.ALL.name().equals(value)) {
            return value;
        }

        if (PayableStatus.PENDING.name().equals(value)) {
            return value;
        }

        if (PayableStatus.PAID.name().equals(value)) {
            return value;
        }

        if (PayableStatus.OVERDUE.name().equals(value)) {
            return value;
        }

        if (PayableStatus.PARTIALLY_PAID.name().equals(value)) {
            return value;
        }

        if (PayableStatus.CANCELED.name().equals(value)) {
            return value;
        }

        return PayableStatus.ALL.name();
    }

    private String normalizePeriodType(String periodType) {
        if (periodType == null || periodType.trim().isEmpty()) {
            return "DUE_DATE";
        }

        String value = periodType.trim().toUpperCase();

        if ("DUE".equals(value)) {
            return "DUE_DATE";
        }

        if ("PAYMENT".equals(value)) {
            return "PAYMENT_DATE";
        }

        if ("CREATED".equals(value)) {
            return "CREATED_DATE";
        }

        if ("DUE_DATE".equals(value)) {
            return value;
        }

        if ("PAYMENT_DATE".equals(value)) {
            return value;
        }

        if ("CREATED_DATE".equals(value)) {
            return value;
        }

        return "DUE_DATE";
    }

    private String normalizeOrderBy(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return "dueDate";
        }

        String value = orderBy.trim();
        if ("dueDate".equals(value)) {
            return value;
        }

        if ("paymentDate".equals(value)) {
            return value;
        }

        if ("createdDate".equals(value)) {
            return value;
        }

        if ("amount".equals(value)) {
            return value;
        }

        if ("description".equals(value)) {
            return value;
        }

        return "dueDate";
    }

    private String normalizeDirection(String direction) {
        if ("DESC".equalsIgnoreCase(direction)) {
            return "DESC";
        }

        return "ASC";
    }

    private Long normalizeId(Long id) {
        if (id == null) {
            return null;
        }

        if (id <= 0) {
            return null;
        }

        return id;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}
