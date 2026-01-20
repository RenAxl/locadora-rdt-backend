package com.locadora_rdt_backend.repositories;

import com.locadora_rdt_backend.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
