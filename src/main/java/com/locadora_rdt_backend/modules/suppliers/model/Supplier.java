package com.locadora_rdt_backend.modules.suppliers.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_supplier")
public class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "trade_name", nullable = false, length = 100)
    private String tradeName;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(unique = true, nullable = false, length = 14)
    private String cnpj;

    @Embedded
    private Address address;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] image;

    @Column(name = "image_content_type", length = 100)
    private String imageContentType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierFile> files = new ArrayList<>();

    public Supplier() {
        // Required by frameworks and serializers.
    }

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getVersion() { return version; }
    public String getName() { return name; }
    public String getTradeName() { return tradeName; }
    public String getCompanyName() { return companyName; }
    public String getCnpj() { return cnpj; }
    public Address getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public byte[] getImage() { return image; }
    public String getImageContentType() { return imageContentType; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public List<SupplierFile> getFiles() { return files; }

    public void setId(Long id) { this.id = id; }
    public void setVersion(Long version) { this.version = version; }
    public void setName(String name) { this.name = name; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public void setAddress(Address address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setImage(byte[] image) { this.image = image; }
    public void setImageContentType(String imageContentType) { this.imageContentType = imageContentType; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public void setFiles(List<SupplierFile> files) { this.files = files; }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Supplier)) return false;
        Supplier other = (Supplier) obj;
        return id != null && id.equals(other.id);
    }
}
