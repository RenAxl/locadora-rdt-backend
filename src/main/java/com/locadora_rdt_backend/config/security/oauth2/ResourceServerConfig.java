package com.locadora_rdt_backend.config.security.oauth2;

import java.util.Arrays;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String USERS_ENDPOINT = "/users/**";
    private static final String USER_WRITE = "USER_WRITE";
    private static final String ROLES_ENDPOINT = "/roles/**";
    private static final String CUSTOMERS_ENDPOINT = "/customers/**";
    private static final String CUSTOMER_FILES_ENDPOINT = "/customers/*/files/**";
    private static final String EMPLOYEES_ENDPOINT = "/employees/**";
    private static final String EMPLOYEE_FILES_ENDPOINT = "/employees/*/files/**";
    private static final String SUPPLIERS_ENDPOINT = "/suppliers/**";
    private static final String SUPPLIER_FILES_ENDPOINT = "/suppliers/*/files/**";
    private static final String RECEIVABLES_ENDPOINT = "/receivables/**";

    private final Environment env;

    public ResourceServerConfig(Environment env) {
        this.env = env;
    }

    @Override
    @SuppressWarnings("java:S4502")
    public void configure(HttpSecurity http) throws Exception {
        // Stateless API authentication is handled by OAuth2 bearer tokens, not browser sessions.

        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers().frameOptions().disable();
        }

        http
                .authorizeRequests()
                .antMatchers("/oauth/token").permitAll()
                .antMatchers("/h2-console", "/h2-console/**").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/activate").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()
                .antMatchers(HttpMethod.POST, "/customer-accounts").permitAll()
                .antMatchers(HttpMethod.POST, "/customer-accounts/create-password").permitAll()
                .antMatchers(HttpMethod.POST, "/customer-accounts/resend-activation").permitAll()

                .antMatchers("/users/me", "/users/me/**").authenticated()

                .antMatchers(HttpMethod.GET, USERS_ENDPOINT)
                .hasAnyAuthority(
                        "USER_READ",
                        USER_WRITE,
                        "USER_DELETE",
                        "USER_STATUS_CHANGE"
                )
                .antMatchers(HttpMethod.POST, USERS_ENDPOINT).hasAuthority(USER_WRITE)
                .antMatchers(HttpMethod.PUT, USERS_ENDPOINT).hasAuthority(USER_WRITE)
                .antMatchers(HttpMethod.DELETE, USERS_ENDPOINT).hasAuthority("USER_DELETE")
                .antMatchers(HttpMethod.PATCH, "/users/*/active").hasAuthority("USER_STATUS_CHANGE")

                .antMatchers(HttpMethod.GET, "/rentals/current-customer")
                .authenticated()
                .antMatchers(HttpMethod.POST, "/rentals/shipping/calculate")
                .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_CLIENTE")
                .antMatchers(HttpMethod.POST, "/rentals")
                .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_CLIENTE")
                .antMatchers(HttpMethod.PATCH, "/rentals/*/confirm")
                .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_CLIENTE")
                .antMatchers(HttpMethod.GET, "/rentals/availability/**")
                .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_CLIENTE")
                .antMatchers("/rentals/**").hasAuthority("ROLE_ADMINISTRADOR")

                .antMatchers("/inventory/**").hasAuthority("ROLE_ADMINISTRADOR")

                .antMatchers("/reports/financial-reports/**")
                .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_FINANCEIRO")
                .antMatchers("/reports/inventory-reports/**").hasAuthority("ROLE_ADMINISTRADOR")

                .antMatchers("/system-settings/**").hasAuthority("ROLE_ADMINISTRADOR")

                .antMatchers(HttpMethod.GET, ROLES_ENDPOINT).hasAuthority("ROLE_READ")
                .antMatchers(HttpMethod.POST, ROLES_ENDPOINT).hasAuthority("ROLE_WRITE")
                .antMatchers(HttpMethod.PUT, ROLES_ENDPOINT).hasAuthority("ROLE_WRITE")
                .antMatchers(HttpMethod.DELETE, ROLES_ENDPOINT).hasAuthority("ROLE_DELETE")

                .antMatchers(HttpMethod.GET, "/permissions/**").hasAuthority("PERMISSION_READ")

                .antMatchers(HttpMethod.GET, CUSTOMER_FILES_ENDPOINT).hasAuthority("CUSTOMER_FILE_READ")
                .antMatchers(HttpMethod.POST, CUSTOMER_FILES_ENDPOINT).hasAuthority("CUSTOMER_FILE_WRITE")
                .antMatchers(HttpMethod.DELETE, CUSTOMER_FILES_ENDPOINT).hasAuthority("CUSTOMER_FILE_DELETE")
                .antMatchers(HttpMethod.PATCH, "/customers/*/active").hasAuthority("CUSTOMER_STATUS_CHANGE")
                .antMatchers(HttpMethod.GET, CUSTOMERS_ENDPOINT).hasAuthority("CUSTOMER_READ")
                .antMatchers(HttpMethod.POST, CUSTOMERS_ENDPOINT).hasAuthority("CUSTOMER_WRITE")
                .antMatchers(HttpMethod.PUT, CUSTOMERS_ENDPOINT).hasAuthority("CUSTOMER_WRITE")
                .antMatchers(HttpMethod.DELETE, CUSTOMERS_ENDPOINT).hasAuthority("CUSTOMER_DELETE")

                .antMatchers(HttpMethod.GET, EMPLOYEE_FILES_ENDPOINT).hasAuthority("EMPLOYEE_FILE_READ")
                .antMatchers(HttpMethod.POST, EMPLOYEE_FILES_ENDPOINT).hasAuthority("EMPLOYEE_FILE_WRITE")
                .antMatchers(HttpMethod.DELETE, EMPLOYEE_FILES_ENDPOINT).hasAuthority("EMPLOYEE_FILE_DELETE")
                .antMatchers(HttpMethod.PATCH, "/employees/*/active").hasAuthority("EMPLOYEE_STATUS_CHANGE")
                .antMatchers(HttpMethod.GET, EMPLOYEES_ENDPOINT).hasAuthority("EMPLOYEE_READ")
                .antMatchers(HttpMethod.POST, EMPLOYEES_ENDPOINT).hasAuthority("EMPLOYEE_WRITE")
                .antMatchers(HttpMethod.PUT, EMPLOYEES_ENDPOINT).hasAuthority("EMPLOYEE_WRITE")
                .antMatchers(HttpMethod.DELETE, EMPLOYEES_ENDPOINT).hasAuthority("EMPLOYEE_DELETE")

                .antMatchers(HttpMethod.GET, "/positions/**").hasAuthority("POSITION_READ")
                .antMatchers(HttpMethod.POST, "/positions/**").hasAuthority("POSITION_WRITE")
                .antMatchers(HttpMethod.PUT, "/positions/**").hasAuthority("POSITION_WRITE")
                .antMatchers(HttpMethod.DELETE, "/positions/**").hasAuthority("POSITION_DELETE")

                .antMatchers(HttpMethod.GET, "/departments/**").hasAuthority("DEPARTMENT_READ")
                .antMatchers(HttpMethod.POST, "/departments/**").hasAuthority("DEPARTMENT_WRITE")
                .antMatchers(HttpMethod.PUT, "/departments/**").hasAuthority("DEPARTMENT_WRITE")
                .antMatchers(HttpMethod.DELETE, "/departments/**").hasAuthority("DEPARTMENT_DELETE")

                .antMatchers(HttpMethod.GET, SUPPLIER_FILES_ENDPOINT).hasAuthority("SUPPLIER_FILE_READ")
                .antMatchers(HttpMethod.POST, SUPPLIER_FILES_ENDPOINT).hasAuthority("SUPPLIER_FILE_WRITE")
                .antMatchers(HttpMethod.DELETE, SUPPLIER_FILES_ENDPOINT).hasAuthority("SUPPLIER_FILE_DELETE")
                .antMatchers(HttpMethod.PUT, "/suppliers/*/image").hasAuthority("SUPPLIER_IMAGE_WRITE")
                .antMatchers(HttpMethod.GET, SUPPLIERS_ENDPOINT).hasAuthority("SUPPLIER_READ")
                .antMatchers(HttpMethod.POST, SUPPLIERS_ENDPOINT).hasAuthority("SUPPLIER_WRITE")
                .antMatchers(HttpMethod.PUT, SUPPLIERS_ENDPOINT).hasAuthority("SUPPLIER_WRITE")
                .antMatchers(HttpMethod.DELETE, SUPPLIERS_ENDPOINT).hasAuthority("SUPPLIER_DELETE")

                .antMatchers(HttpMethod.GET, RECEIVABLES_ENDPOINT).hasAuthority("RECEIVABLE_READ")
                .antMatchers(HttpMethod.POST, RECEIVABLES_ENDPOINT).hasAuthority("RECEIVABLE_WRITE")
                .antMatchers(HttpMethod.PUT, RECEIVABLES_ENDPOINT).hasAuthority("RECEIVABLE_WRITE")
                .antMatchers(HttpMethod.DELETE, RECEIVABLES_ENDPOINT).hasAuthority("RECEIVABLE_DELETE")

                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .headers().frameOptions().sameOrigin();
    }
}
