package com.locadora_rdt_backend.modules.receivables.repository;

import com.locadora_rdt_backend.modules.receivables.model.ReceivableFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceivableFileRepository extends JpaRepository<ReceivableFile, Long> {

    List<ReceivableFile> findByReceivableIdOrderByIdDesc(Long receivableId);
}
