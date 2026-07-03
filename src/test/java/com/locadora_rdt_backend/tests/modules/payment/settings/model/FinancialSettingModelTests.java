package com.locadora_rdt_backend.tests.modules.payment.settings.model;

import com.locadora_rdt_backend.modules.financial.payment.settings.model.FinancialSetting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class FinancialSettingModelTests {

    @Test
    void prePersistShouldFillDefaultsWhenNullableFieldsAreNull() {
        FinancialSetting setting = new FinancialSetting();
        setting.setSingletonKey(null);
        setting.setDefaultLateFeePercent(null);
        setting.setDefaultLateInterestPercent(null);

        setting.prePersist();

        Assertions.assertNotNull(setting.getCreatedAt());
        Assertions.assertEquals(FinancialSetting.DEFAULT_SINGLETON_KEY, setting.getSingletonKey());
        Assertions.assertEquals(BigDecimal.ZERO, setting.getDefaultLateFeePercent());
        Assertions.assertEquals(BigDecimal.ZERO, setting.getDefaultLateInterestPercent());
    }

    @Test
    void prePersistShouldPreserveExplicitValues() {
        FinancialSetting setting = new FinancialSetting();
        setting.setSingletonKey("CUSTOM");
        setting.setDefaultLateFeePercent(new BigDecimal("2.00"));
        setting.setDefaultLateInterestPercent(new BigDecimal("1.50"));

        setting.prePersist();

        Assertions.assertEquals("CUSTOM", setting.getSingletonKey());
        Assertions.assertEquals(new BigDecimal("2.00"), setting.getDefaultLateFeePercent());
        Assertions.assertEquals(new BigDecimal("1.50"), setting.getDefaultLateInterestPercent());
    }

    @Test
    void preUpdateShouldSetUpdatedAt() {
        FinancialSetting setting = new FinancialSetting();

        setting.preUpdate();

        Assertions.assertNotNull(setting.getUpdatedAt());
    }

    @Test
    void equalsShouldHandleIdentityNullAndDifferentIds() {
        FinancialSetting first = new FinancialSetting();
        FinancialSetting second = new FinancialSetting();

        Assertions.assertEquals(first, first);
        Assertions.assertEquals(first, second);
        Assertions.assertNotEquals(first, null);
        Assertions.assertNotEquals(first, "setting");

        first.setId(1L);
        Assertions.assertNotEquals(first, second);

        second.setId(2L);
        Assertions.assertNotEquals(first, second);

        second.setId(1L);
        Assertions.assertEquals(first, second);
        Assertions.assertEquals(first.hashCode(), second.hashCode());
    }
}
