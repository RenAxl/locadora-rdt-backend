package com.locadora_rdt_backend.modules.organization.employees.constants;

public final class EmployeeAuthorizationExpressions {

    private EmployeeAuthorizationExpressions() {
    }

    public static final String EMPLOYEES_READ =
            "hasAuthority('EMPLOYEES_READ')";

    public static final String EMPLOYEES_WRITE =
            "hasAuthority('EMPLOYEES_WRITE')";

    public static final String EMPLOYEES_DELETE =
            "hasAuthority('EMPLOYEES_DELETE')";
}
