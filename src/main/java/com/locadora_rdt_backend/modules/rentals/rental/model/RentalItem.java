package com.locadora_rdt_backend.modules.rentals.rental.model;

import com.locadora_rdt_backend.modules.stocks.items.model.Item;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_rental_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"rental_id", "item_id"})
})
public class RentalItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discount;

    @Column(name = "additional_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal additionalFee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public Long getId() { return id; }
    public Rental getRental() { return rental; }
    public Item getItem() { return item; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getAdditionalFee() { return additionalFee; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setId(Long id) { this.id = id; }
    public void setRental(Rental rental) { this.rental = rental; }
    public void setItem(Item item) { this.item = item; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public void setAdditionalFee(BigDecimal additionalFee) { this.additionalFee = additionalFee; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
