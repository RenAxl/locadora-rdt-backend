package com.locadora_rdt_backend.modules.payment.frequencies.repository;

import com.locadora_rdt_backend.modules.payment.frequencies.model.PaymentFrequency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentFrequencyRepository extends JpaRepository<PaymentFrequency, Long> {

    @Query("select paymentFrequency from PaymentFrequency paymentFrequency where lower(paymentFrequency.frequency) like lower(concat('%', ?1, '%'))")
    Page<PaymentFrequency> find(String frequency, Pageable pageable);

    @Modifying
    @Query("DELETE FROM PaymentFrequency paymentFrequency WHERE paymentFrequency.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);
}
