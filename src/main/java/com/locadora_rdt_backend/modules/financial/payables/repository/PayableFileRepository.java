package com.locadora_rdt_backend.modules.financial.payables.repository;

import com.locadora_rdt_backend.modules.financial.payables.model.PayableFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayableFileRepository extends JpaRepository<PayableFile, Long> {

    List<PayableFile> findByPayableIdOrderByIdDesc(Long payableId);
}
