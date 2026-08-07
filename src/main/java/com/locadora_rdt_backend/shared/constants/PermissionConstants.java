package com.locadora_rdt_backend.shared.constants;

public final class PermissionConstants {

    private PermissionConstants() {
    }

    // Financial
    public static final String PAYABLES_READ = "hasAuthority('PAYABLES_READ')";
    public static final String PAYABLES_WRITE = "hasAuthority('PAYABLES_WRITE')";
    public static final String PAYABLES_DELETE = "hasAuthority('PAYABLES_DELETE')";

    public static final String FREQUENCIES_READ = "hasAuthority('FREQUENCIES_READ')";
    public static final String FREQUENCIES_WRITE = "hasAuthority('FREQUENCIES_WRITE')";
    public static final String FREQUENCIES_DELETE = "hasAuthority('FREQUENCIES_DELETE')";

    public static final String METHODS_READ = "hasAuthority('METHODS_READ')";
    public static final String METHODS_WRITE = "hasAuthority('METHODS_WRITE')";
    public static final String METHODS_DELETE = "hasAuthority('METHODS_DELETE')";

    public static final String RECEIVABLES_READ = "hasAuthority('RECEIVABLES_READ')";
    public static final String RECEIVABLES_WRITE = "hasAuthority('RECEIVABLES_WRITE')";
    public static final String RECEIVABLES_DELETE = "hasAuthority('RECEIVABLES_DELETE')";

    // Identity
    public static final String ROLE_READ = "hasAuthority('ROLE_READ')";
    public static final String ROLE_WRITE = "hasAuthority('ROLE_WRITE')";
    public static final String ROLE_DELETE = "hasAuthority('ROLE_DELETE')";

    public static final String USER_READ = "hasAuthority('USER_READ')";
    public static final String USER_WRITE = "hasAuthority('USER_WRITE')";
    public static final String USER_DELETE = "hasAuthority('USER_DELETE')";
    public static final String USER_PROFILE_READ = "hasAuthority('USER_PROFILE_READ')";
    public static final String USER_PROFILE_WRITE = "hasAuthority('USER_PROFILE_WRITE')";

    // Organization
    public static final String CUSTOMERS_READ = "hasAuthority('CUSTOMERS_READ')";
    public static final String CUSTOMERS_WRITE = "hasAuthority('CUSTOMERS_WRITE')";
    public static final String CUSTOMERS_DELETE = "hasAuthority('CUSTOMERS_DELETE')";

    public static final String DEPARTMENTS_READ = "hasAuthority('DEPARTMENTS_READ')";
    public static final String DEPARTMENTS_WRITE = "hasAuthority('DEPARTMENTS_WRITE')";
    public static final String DEPARTMENTS_DELETE = "hasAuthority('DEPARTMENTS_DELETE')";

    public static final String EMPLOYEES_READ = "hasAuthority('EMPLOYEES_READ')";
    public static final String EMPLOYEES_WRITE = "hasAuthority('EMPLOYEES_WRITE')";
    public static final String EMPLOYEES_DELETE = "hasAuthority('EMPLOYEES_DELETE')";

    public static final String POSITIONS_READ = "hasAuthority('POSITIONS_READ')";
    public static final String POSITIONS_WRITE = "hasAuthority('POSITIONS_WRITE')";
    public static final String POSITIONS_DELETE = "hasAuthority('POSITIONS_DELETE')";

    public static final String SUPPLIERS_READ = "hasAuthority('SUPPLIERS_READ')";
    public static final String SUPPLIERS_WRITE = "hasAuthority('SUPPLIERS_WRITE')";
    public static final String SUPPLIERS_DELETE = "hasAuthority('SUPPLIERS_DELETE')";

    // Rentals
    public static final String CATALOG_READ = "hasAuthority('CATALOG_READ')";
    public static final String CATALOG_WRITE = "hasAuthority('CATALOG_WRITE')";
    public static final String CATALOG_DELETE = "hasAuthority('CATALOG_DELETE')";

    public static final String RENTALS_READ = "hasAuthority('RENTALS_READ')";
    public static final String RENTALS_WRITE = "hasAuthority('RENTALS_WRITE')";
    public static final String RENTALS_DELETE = "hasAuthority('RENTALS_DELETE')";
    public static final String CUSTOMER_RENTALS = "hasAuthority('CUSTOMER_RENTALS')";
    public static final String RENTALS_HISTORY_READ = "hasAuthority('RENTALS_HISTORY_READ')";

    public static final String RENTAL_TYPES_READ = "hasAuthority('RENTAL_TYPES_READ')";
    public static final String RENTAL_TYPES_WRITE = "hasAuthority('RENTAL_TYPES_WRITE')";
    public static final String RENTAL_TYPES_DELETE = "hasAuthority('RENTAL_TYPES_DELETE')";
    public static final String CUSTOMERS_RENTAL_TYPES_READ =
            "hasAuthority('CUSTOMERS_RENTAL_TYPES_READ')";

    // Reports
    public static final String FINANCIALREPORTS_READ = "hasAuthority('FINANCIALREPORTS_READ')";
    public static final String INVENTORYREPORTS_READ = "hasAuthority('INVENTORYREPORTS_READ')";

    // Settings
    public static final String FINANCIALSETTINGS_READ = "hasAuthority('FINANCIALSETTINGS_READ')";
    public static final String FINANCIALSETTINGS_WRITE = "hasAuthority('FINANCIALSETTINGS_WRITE')";
    public static final String SYSTEMSETTINGS_READ = "hasAuthority('SYSTEMSETTINGS_READ')";
    public static final String SYSTEMSETTINGS_WRITE = "hasAuthority('SYSTEMSETTINGS_WRITE')";

    // Stocks
    public static final String CATEGORIES_READ = "hasAuthority('CATEGORIES_READ')";
    public static final String CUSTOMERS_CATEGORIES_READ =
            "hasAuthority('CUSTOMERS_CATEGORIES_READ')";
    public static final String CATEGORIES_WRITE = "hasAuthority('CATEGORIES_WRITE')";
    public static final String CATEGORIES_DELETE = "hasAuthority('CATEGORIES_DELETE')";

    public static final String ITEMS_READ = "hasAuthority('ITEMS_READ')";
    public static final String CUSTOMERS_ITEMS_READ = "hasAuthority('CUSTOMERS_ITEMS_READ')";
    public static final String ITEMS_WRITE = "hasAuthority('ITEMS_WRITE')";
    public static final String ITEMS_DELETE = "hasAuthority('ITEMS_DELETE')";

    public static final String STOCKBALANCES_READ = "hasAuthority('STOCKBALANCES_READ')";
    public static final String STOCKBALANCES_WRITE = "hasAuthority('STOCKBALANCES_WRITE')";
    public static final String STOCKBALANCES_DELETE = "hasAuthority('STOCKBALANCES_DELETE')";

    public static final String STOCKMOVEMENTS_READ = "hasAuthority('STOCKMOVEMENTS_READ')";
    public static final String STOCKMOVEMENTS_WRITE = "hasAuthority('STOCKMOVEMENTS_WRITE')";
    public static final String STOCKMOVEMENTS_DELETE = "hasAuthority('STOCKMOVEMENTS_DELETE')";
}
