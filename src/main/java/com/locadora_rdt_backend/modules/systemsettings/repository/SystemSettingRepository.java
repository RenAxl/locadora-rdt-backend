package com.locadora_rdt_backend.modules.systemsettings.repository;

import com.locadora_rdt_backend.modules.systemsettings.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    @Query(value = "SELECT * FROM tb_system_setting WHERE singleton_key = :singletonKey LIMIT 1", nativeQuery = true)
    Optional<SystemSetting> findBySingletonKey(@Param("singletonKey") String singletonKey);
}
