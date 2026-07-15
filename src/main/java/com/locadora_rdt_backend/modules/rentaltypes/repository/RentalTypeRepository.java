package com.locadora_rdt_backend.modules.rentaltypes.repository;

import com.locadora_rdt_backend.modules.rentaltypes.model.RentalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalTypeRepository extends JpaRepository<RentalType, Long> {

    @Query("select rentalType from RentalType rentalType where lower(rentalType.name) like lower(concat('%', ?1, '%'))")
    Page<RentalType> find(String name, Pageable pageable);

    @Modifying
    @Query("DELETE FROM RentalType rentalType WHERE rentalType.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE RentalType rentalType SET rentalType.active = :active WHERE rentalType.id = :id")
    int updateActiveById(@Param("id") Long id,
                         @Param("active") boolean active);
}
