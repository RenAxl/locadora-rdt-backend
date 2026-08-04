package com.locadora_rdt_backend.modules.rentals.rentalhistory.repository;

import com.locadora_rdt_backend.modules.rentals.rental.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalHistoryRepository extends JpaRepository<Rental, Long> {

    @Query(
            value = "SELECT rental.* " +
                    "FROM tb_rental rental " +
                    "INNER JOIN tb_customer customer ON customer.id = rental.customer_id " +
                    "WHERE LOWER(customer.email) = LOWER(:email)",
            countQuery = "SELECT COUNT(rental.id) " +
                    "FROM tb_rental rental " +
                    "INNER JOIN tb_customer customer ON customer.id = rental.customer_id " +
                    "WHERE LOWER(customer.email) = LOWER(:email)",
            nativeQuery = true
    )
    Page<Rental> findByCustomerEmailIgnoreCase(@Param("email") String email, Pageable pageable);
}
