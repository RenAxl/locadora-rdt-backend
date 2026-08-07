package com.locadora_rdt_backend.modules.financial.receivables.service;

import com.locadora_rdt_backend.modules.financial.receivables.dto.ReceivableFilterDTO;
import com.locadora_rdt_backend.modules.financial.receivables.model.ReceivableStatus;
import org.springframework.stereotype.Component;

import static com.locadora_rdt_backend.modules.financial.receivables.constants.ReceivableConstants.*;

@Component
public class ReceivableFilterNormalizer {

    public ReceivableFilterDTO normalize(ReceivableFilterDTO filters) {
        ReceivableFilterDTO source;

        if (filters == null) {
            source = new ReceivableFilterDTO();
        } else {
            source = filters;
        }

        ReceivableFilterDTO normalized = new ReceivableFilterDTO();

        normalized.setSearch(trimToNull(source.getSearch()));
        normalized.setStartDate(source.getStartDate());
        normalized.setEndDate(source.getEndDate());
        normalized.setStatus(normalizeStatus(source.getStatus()));
        normalized.setPeriodType(normalizePeriodType(source.getPeriodType()));
        normalized.setCustomerId(normalizeId(source.getCustomerId()));
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
            return ReceivableStatus.ALL.name();
        }

        String value = status.trim().toUpperCase();

        if (STATUS_OPEN.equals(value)) {
            return ReceivableStatus.PENDING.name();
        }

        if (ReceivableStatus.ALL.name().equals(value)) {
            return value;
        }

        if (ReceivableStatus.PENDING.name().equals(value)) {
            return value;
        }

        if (ReceivableStatus.PAID.name().equals(value)) {
            return value;
        }

        if (ReceivableStatus.OVERDUE.name().equals(value)) {
            return value;
        }

        if (ReceivableStatus.PARTIALLY_PAID.name().equals(value)) {
            return value;
        }

        if (ReceivableStatus.CANCELED.name().equals(value)) {
            return value;
        }

        return ReceivableStatus.ALL.name();
    }

    private String normalizePeriodType(String periodType) {
        if (periodType == null || periodType.trim().isEmpty()) {
            return PERIOD_DUE_DATE;
        }

        String value = periodType.trim().toUpperCase();

        if (PERIOD_DUE.equals(value)) {
            return PERIOD_DUE_DATE;
        }

        if (PERIOD_PAYMENT.equals(value)) {
            return PERIOD_PAYMENT_DATE;
        }

        if (PERIOD_CREATED.equals(value)) {
            return PERIOD_CREATED_DATE;
        }

        if (PERIOD_DUE_DATE.equals(value)) {
            return value;
        }

        if (PERIOD_PAYMENT_DATE.equals(value)) {
            return value;
        }

        if (PERIOD_CREATED_DATE.equals(value)) {
            return value;
        }

        return PERIOD_DUE_DATE;
    }

    private String normalizeOrderBy(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return ORDER_BY_DUE_DATE;
        }

        String value = orderBy.trim();
        if (ORDER_BY_DUE_DATE.equals(value)) {
            return value;
        }

        if (ORDER_BY_PAYMENT_DATE.equals(value)) {
            return value;
        }

        if (ORDER_BY_CREATED_DATE.equals(value)) {
            return value;
        }

        if (ORDER_BY_AMOUNT.equals(value)) {
            return value;
        }

        if (ORDER_BY_DESCRIPTION.equals(value)) {
            return value;
        }

        return ORDER_BY_DUE_DATE;
    }

    private String normalizeDirection(String direction) {
        if (DIRECTION_DESC.equalsIgnoreCase(direction)) {
            return DIRECTION_DESC;
        }

        return DIRECTION_ASC;
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
