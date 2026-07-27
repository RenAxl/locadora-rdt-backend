package com.locadora_rdt_backend.modules.settings.financialsettings.repository;

import com.locadora_rdt_backend.modules.settings.financialsettings.model.FinancialSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinancialSettingRepository extends JpaRepository<FinancialSetting, Long> {

    Optional<FinancialSetting> findBySingletonKey(String singletonKey);
}
