package com.locadora_rdt_backend.modules.inventory.stockbalances.model;

import com.locadora_rdt_backend.modules.inventory.items.model.Item;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "tb_stock_balance")
public class StockBalance implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private Item item;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Column(name = "unavailable_quantity", nullable = false)
    private Integer unavailableQuantity;

    @Column(name = "minimum_quantity", nullable = false)
    private Integer minimumQuantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    public StockBalance() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public Item getItem() {
        return item;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public Integer getUnavailableQuantity() {
        return unavailableQuantity;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
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

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public void setUnavailableQuantity(Integer unavailableQuantity) {
        this.unavailableQuantity = unavailableQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
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
}
