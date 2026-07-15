package com.locadora_rdt_backend.modules.categories.repository;

import com.locadora_rdt_backend.modules.categories.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select category from Category category where lower(category.name) like lower(concat('%', ?1, '%'))")
    Page<Category> find(String name, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Category category WHERE category.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE Category category SET category.active = :active WHERE category.id = :id")
    int updateActiveById(@Param("id") Long id,
                         @Param("active") boolean active);
}
