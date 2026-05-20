package com.locadora_rdt_backend.modules.positions.repository;

import com.locadora_rdt_backend.modules.positions.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PositionRepository extends JpaRepository<Position, Long> {

    @Query(
            value =
                    "SELECT * " +
                            "FROM tb_position " +
                            "WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))",

            countQuery =
                    "SELECT COUNT(*) " +
                            "FROM tb_position " +
                            "WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))",

            nativeQuery = true
    )
    Page<Position> searchByName(
            String name,
            Pageable pageable
    );
}