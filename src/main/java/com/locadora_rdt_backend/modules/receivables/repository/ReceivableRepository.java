package com.locadora_rdt_backend.modules.receivables.repository;

import com.locadora_rdt_backend.modules.receivables.model.Receivable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivableRepository extends JpaRepository<Receivable, Long> {
}
