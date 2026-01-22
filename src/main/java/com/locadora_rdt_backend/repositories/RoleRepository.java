package com.locadora_rdt_backend.repositories;

import com.locadora_rdt_backend.dto.RoleListDTO;
import com.locadora_rdt_backend.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query(
            "SELECT new com.locadora_rdt_backend.dto.RoleListDTO(" +
                    " r.id, " +
                    " r.authority, " +
                    " COUNT(p) " +
                    ") " +
                    "FROM Role r " +
                    "LEFT JOIN r.permissions p " +
                    "GROUP BY r.id, r.authority"
    )
    List<RoleListDTO> findAllWithPermissionsCount();

}
