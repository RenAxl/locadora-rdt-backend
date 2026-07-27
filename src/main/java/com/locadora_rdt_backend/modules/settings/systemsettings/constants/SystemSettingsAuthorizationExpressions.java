package com.locadora_rdt_backend.modules.settings.systemsettings.constants;

public final class SystemSettingsAuthorizationExpressions {

    private SystemSettingsAuthorizationExpressions() {
    }

    public static final String SYSTEMSETTINGS_READ =
            "hasAuthority('SYSTEMSETTINGS_READ')";

    public static final String SYSTEMSETTINGS_WRITE =
            "hasAuthority('SYSTEMSETTINGS_WRITE')";

}
