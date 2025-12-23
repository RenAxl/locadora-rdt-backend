package com.locadora_rdt_backend.repositories;

import com.locadora_rdt_backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select use from User use where use.name like %?1%")
    Page<User> find(String name, Pageable pageable);

    User findByEmail(String email);

    User findByTelephone(String telephone);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);
}
