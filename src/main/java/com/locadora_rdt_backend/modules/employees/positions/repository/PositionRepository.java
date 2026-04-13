package com.locadora_rdt_backend.modules.employees.positions.repository;

import com.locadora_rdt_backend.modules.employees.positions.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PositionRepository extends JpaRepository<Position, Long> {

    @Query("select position from Position position where position.name like %?1%")
    Page<Position> find(String name, Pageable pageable);

}