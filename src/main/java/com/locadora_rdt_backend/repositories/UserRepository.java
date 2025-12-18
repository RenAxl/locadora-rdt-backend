package com.locadora_rdt_backend.repositories;

import com.locadora_rdt_backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select use from User use where use.name like %?1%")
    Page<User> find(String name, Pageable pageable);

    User findByEmail(String email);

}
