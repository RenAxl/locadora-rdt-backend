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
                .antMatchers(HttpMethod.POST, "/users/activate").permitAll()
                .antMatchers(HttpMethod.POST, "/users/forgot-password").permitAll()
                .antMatchers(HttpMethod.POST, "/users/reset-password").permitAll()
                .antMatchers(HttpMethod.GET, "/users/me").authenticated()

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
                .antMatchers(HttpMethod.PUT, "/users/me/password").authenticated()

                .antMatchers(HttpMethod.POST, "/rentals")
                .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_CLIENTE")
                .antMatchers(HttpMethod.GET, "/rentals/current-customer")
                .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_CLIENTE")
                .antMatchers(HttpMethod.PATCH, "/rentals/*/confirm")
                .hasAnyAuthority("ROLE_ADMINISTRADOR", "ROLE_CLIENTE")

                .antMatchers(HttpMethod.GET, ROLES_ENDPOINT).hasAuthority("ROLE_READ")
                .antMatchers(HttpMethod.POST, ROLES_ENDPOINT).hasAuthority("ROLE_WRITE")
                .antMatchers(HttpMethod.PUT, ROLES_ENDPOINT).hasAuthority("ROLE_WRITE")

                .antMatchers(HttpMethod.GET, "/permissions/**").hasAuthority("PERMISSION_READ")

                .antMatchers(HttpMethod.GET, CUSTOMERS_ENDPOINT).hasAuthority("CUSTOMER_READ")
                .antMatchers(HttpMethod.PUT, CUSTOMERS_ENDPOINT).hasAuthority("CUSTOMER_WRITE")
                .antMatchers(HttpMethod.DELETE, CUSTOMERS_ENDPOINT).hasAuthority("CUSTOMER_DELETE")
                .antMatchers(HttpMethod.PATCH, "/customers/*/active").hasAuthority("CUSTOMER_STATUS_CHANGE")
                .antMatchers(HttpMethod.GET, CUSTOMER_FILES_ENDPOINT).hasAuthority("CUSTOMER_FILE_READ")
                .antMatchers(HttpMethod.POST, CUSTOMER_FILES_ENDPOINT).hasAuthority("CUSTOMER_FILE_WRITE")
                .antMatchers(HttpMethod.DELETE, CUSTOMER_FILES_ENDPOINT).hasAuthority("CUSTOMER_FILE_DELETE")


               // .antMatchers(HttpMethod.GET, "/employees/**").hasAuthority("EMPLOYEE_READ")
                //.antMatchers(HttpMethod.PUT, "/employees/**").hasAuthority("EMPLOYEE_WRITE")
                //.antMatchers(HttpMethod.DELETE, "/employees/**").hasAuthority("EMPLOYEE_DELETE")
               // .antMatchers(HttpMethod.PATCH, "/employees/*/active").hasAuthority("EMPLOYEE_STATUS_CHANGE")
               // .antMatchers(HttpMethod.GET, "/employees/*/files/**").hasAuthority("EMPLOYEE_FILE_READ")
               // .antMatchers(HttpMethod.POST, "/employees/*/files/**").hasAuthority("EMPLOYEE_FILE_WRITE")
                //.antMatchers(HttpMethod.DELETE, "/employees/*/files/**").hasAuthority("EMPLOYEE_FILE_DELETE")

                //.antMatchers(HttpMethod.GET, "/positions/**").hasAuthority("POSITION_READ")
                //.antMatchers(HttpMethod.POST, "/positions/**").hasAuthority("POSITION_WRITE")
                //.antMatchers(HttpMethod.PUT, "/positions/**").hasAuthority("POSITION_WRITE")

                //.antMatchers(HttpMethod.GET, "/departments/**").hasAuthority("DEPARTMENT_READ")
                //.antMatchers(HttpMethod.POST, "/departments/**").hasAuthority("DEPARTMENT_WRITE")
                //.antMatchers(HttpMethod.PUT, "/departments/**").hasAuthority("DEPARTMENT_WRITE")

                //.anyRequest().authenticated()
                .and()
                .csrf().disable()
                .headers().frameOptions().sameOrigin();
    }


}
