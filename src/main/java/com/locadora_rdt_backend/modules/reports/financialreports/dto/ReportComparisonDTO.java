package com.locadora_rdt_backend.modules.reports.financialreports.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReportComparisonDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal receivableTotal;
    private BigDecimal payableTotal;
    private BigDecimal balance;
    private Integer receivableCount;
    private Integer payableCount;
    private Integer year;
    private List<ReportComparisonMonthDTO> months = new ArrayList<>();

    public ReportComparisonDTO() {
    }

    public ReportComparisonDTO(
            BigDecimal receivableTotal,
            BigDecimal payableTotal,
            BigDecimal balance,
            Integer receivableCount,
            Integer payableCount,
            Integer year,
            List<ReportComparisonMonthDTO> months
    ) {
        this.receivableTotal = receivableTotal;
        this.payableTotal = payableTotal;
        this.balance = balance;
        this.receivableCount = receivableCount;
        this.payableCount = payableCount;
        this.year = year;
        this.months = months;
    }

    public BigDecimal getReceivableTotal() {
        return receivableTotal;
    }

    public void setReceivableTotal(BigDecimal receivableTotal) {
        this.receivableTotal = receivableTotal;
    }

    public BigDecimal getPayableTotal() {
        return payableTotal;
    }

    public void setPayableTotal(BigDecimal payableTotal) {
        this.payableTotal = payableTotal;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getReceivableCount() {
        return receivableCount;
    }

    public void setReceivableCount(Integer receivableCount) {
        this.receivableCount = receivableCount;
    }

    public Integer getPayableCount() {
        return payableCount;
    }

    public void setPayableCount(Integer payableCount) {
        this.payableCount = payableCount;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<ReportComparisonMonthDTO> getMonths() {
        return months;
    }

    public void setMonths(List<ReportComparisonMonthDTO> months) {
        this.months = months;
    }

    public static class ReportComparisonMonthDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer month;
        private String label;
        private BigDecimal receivableTotal;
        private BigDecimal payableTotal;

        public ReportComparisonMonthDTO() {
        }

        public ReportComparisonMonthDTO(
                Integer month,
                String label,
                BigDecimal receivableTotal,
                BigDecimal payableTotal
        ) {
            this.month = month;
            this.label = label;
            this.receivableTotal = receivableTotal;
            this.payableTotal = payableTotal;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public BigDecimal getReceivableTotal() {
            return receivableTotal;
        }

        public void setReceivableTotal(BigDecimal receivableTotal) {
            this.receivableTotal = receivableTotal;
        }

        public BigDecimal getPayableTotal() {
            return payableTotal;
        }

        public void setPayableTotal(BigDecimal payableTotal) {
            this.payableTotal = payableTotal;
        }
    }
}
