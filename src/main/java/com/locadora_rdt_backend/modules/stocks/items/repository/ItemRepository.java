package com.locadora_rdt_backend.modules.stocks.items.repository;

import com.locadora_rdt_backend.modules.stocks.items.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select item from Item item where lower(item.name) like lower(concat('%', ?1, '%'))")
    Page<Item> find(String name, Pageable pageable);

    @Query("select item from Item item "
            + "where lower(item.name) like lower(concat('%', :name, '%')) "
            + "and (:categoryId = -1L or item.category.id = :categoryId)")
    Page<Item> findForCatalog(@Param("name") String name,
                              @Param("categoryId") Long categoryId,
                              Pageable pageable);

    @Modifying
    @Query("DELETE FROM Item item WHERE item.id IN :ids")
    void deleteAllByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE Item item SET item.active = :active WHERE item.id = :id")
    int updateActiveById(@Param("id") Long id,
                         @Param("active") boolean active);
}
