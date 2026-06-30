package com.locadora_rdt_backend.modules.receivables.repository;

import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReceivableRepository extends JpaRepository<Receivable, Long> {

    @Query("select receivable from Receivable receivable where receivable.description like %?1%")
    Page<Receivable> find(String description, Pageable pageable);
}
