package com.locadora_rdt_backend.modules.inventory.stockmovements.model;

import com.locadora_rdt_backend.modules.inventory.items.model.Item;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "tb_stock_movement")
public class StockMovement implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String reason;

    @Column(name = "reference_type", length = 60)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    public StockMovement() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public String getType() {
        return type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
