package com.locadora_rdt_backend.modules.payment.methods.repository;

import com.locadora_rdt_backend.modules.payment.methods.model.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    @Query("select paymentMethod from PaymentMethod paymentMethod where lower(paymentMethod.name) like lower(concat('%', ?1, '%'))")
    Page<PaymentMethod> find(String name, Pageable pageable);

    @Modifying
    @Query("DELETE FROM PaymentMethod paymentMethod WHERE paymentMethod.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);
}
