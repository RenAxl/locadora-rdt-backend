package com.locadora_rdt_backend.modules.customers.model;

import com.locadora_rdt_backend.shared.model.StoredFile;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_customer_file")
public class CustomerFile extends StoredFile {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public CustomerFile() {
        // Required by frameworks and serializers.
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
