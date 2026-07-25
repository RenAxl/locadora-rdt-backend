package com.locadora_rdt_backend.modules.organization.departments.constants;

public final class DepartmentAuthorizationExpressions {

    private DepartmentAuthorizationExpressions() {
    }

    public static final String DEPARTMENTS_READ =
            "hasAuthority('DEPARTMENTS_READ')";

    public static final String DEPARTMENTS_WRITE =
            "hasAuthority('DEPARTMENTS_WRITE')";

    public static final String DEPARTMENTS_DELETE =
            "hasAuthority('DEPARTMENTS_DELETE')";
}
