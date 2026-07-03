package com.locadora_rdt_backend.modules.financial.payment.settings.repository;

import com.locadora_rdt_backend.modules.financial.payment.settings.model.FinancialSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinancialSettingRepository extends JpaRepository<FinancialSetting, Long> {

    Optional<FinancialSetting> findBySingletonKey(String singletonKey);
}
