package com.locadora_rdt_backend.modules.rentals.rental.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        name = "tb_rental_item_unit",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_rental_item_unit",
                        columnNames = {"rental_item_id", "item_unit_id"}
                )
        }
)
public class RentalItemUnit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_item_id", nullable = false)
    private RentalItem rentalItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_unit_id", nullable = false)
    private ItemUnit itemUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RentalItemUnitStatus status;

    @Column(name = "delivery_condition", length = 500)
    private String deliveryCondition;

    @Column(name = "return_condition", length = 500)
    private String returnCondition;

    @Column(name = "reserved_at")
    private Instant reservedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    public RentalItemUnit() {
    }

    public RentalItemUnit(Long id, RentalItem rentalItem, ItemUnit itemUnit) {
        this.id = id;
        this.rentalItem = rentalItem;
        this.itemUnit = itemUnit;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();

        if (this.status == null) {
            this.status = RentalItemUnitStatus.RESERVED;
        }

        if (this.status == RentalItemUnitStatus.RESERVED
                && this.reservedAt == null) {
            this.reservedAt = Instant.now();
        }
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

    public RentalItem getRentalItem() {
        return rentalItem;
    }

    public ItemUnit getItemUnit() {
        return itemUnit;
    }

    public RentalItemUnitStatus getStatus() {
        return status;
    }

    public String getDeliveryCondition() {
        return deliveryCondition;
    }

    public String getReturnCondition() {
        return returnCondition;
    }

    public Instant getReservedAt() {
        return reservedAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public Instant getReturnedAt() {
        return returnedAt;
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

    public void setRentalItem(RentalItem rentalItem) {
        this.rentalItem = rentalItem;
    }

    public void setItemUnit(ItemUnit itemUnit) {
        this.itemUnit = itemUnit;
    }

    public void setStatus(RentalItemUnitStatus status) {
        this.status = status;
    }

    public void setDeliveryCondition(String deliveryCondition) {
        this.deliveryCondition = deliveryCondition;
    }

    public void setReturnCondition(String returnCondition) {
        this.returnCondition = returnCondition;
    }

    public void setReservedAt(Instant reservedAt) {
        this.reservedAt = reservedAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setReturnedAt(Instant returnedAt) {
        this.returnedAt = returnedAt;
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
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RentalItemUnit)) {
            return false;
        }

        RentalItemUnit other = (RentalItemUnit) obj;
        return id != null && id.equals(other.id);
    }
}