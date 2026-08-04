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

    private final Environment env;

    public ResourceServerConfig(Environment env) {
        this.env = env;
    }

    @Override
    @SuppressWarnings("java:S4502")
    public void configure(HttpSecurity http) throws Exception {

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
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .headers().frameOptions().sameOrigin();
    }
}
