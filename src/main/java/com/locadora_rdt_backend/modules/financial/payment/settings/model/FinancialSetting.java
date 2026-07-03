package com.locadora_rdt_backend.modules.financial.payment.settings.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "tb_financial_setting",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_financial_setting_singleton_key",
                columnNames = "singleton_key"
        )
)
public class FinancialSetting implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_SINGLETON_KEY = "DEFAULT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "singleton_key", nullable = false, unique = true, length = 30)
    private String singletonKey = DEFAULT_SINGLETON_KEY;

    @Column(name = "default_late_fee_percent", nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultLateFeePercent = BigDecimal.ZERO;

    @Column(name = "default_late_interest_percent", nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultLateInterestPercent = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    public FinancialSetting() {
        // Required by frameworks and serializers.
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();

        if (this.singletonKey == null) {
            this.singletonKey = DEFAULT_SINGLETON_KEY;
        }

        if (this.defaultLateFeePercent == null) {
            this.defaultLateFeePercent = BigDecimal.ZERO;
        }

        if (this.defaultLateInterestPercent == null) {
            this.defaultLateInterestPercent = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getSingletonKey() {
        return singletonKey;
    }

    public BigDecimal getDefaultLateFeePercent() {
        return defaultLateFeePercent;
    }

    public BigDecimal getDefaultLateInterestPercent() {
        return defaultLateInterestPercent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSingletonKey(String singletonKey) {
        this.singletonKey = singletonKey;
    }

    public void setDefaultLateFeePercent(BigDecimal defaultLateFeePercent) {
        this.defaultLateFeePercent = defaultLateFeePercent;
    }

    public void setDefaultLateInterestPercent(BigDecimal defaultLateInterestPercent) {
        this.defaultLateInterestPercent = defaultLateInterestPercent;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        FinancialSetting other = (FinancialSetting) obj;

        if (id == null) {
            return other.id == null;
        }

        return id.equals(other.id);
    }
}
