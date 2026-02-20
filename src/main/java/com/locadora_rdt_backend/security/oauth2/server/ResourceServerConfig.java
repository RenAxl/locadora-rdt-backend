package com.locadora_rdt_backend.security.oauth2.server;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private Environment env;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers().frameOptions().disable();
        }

        http
                .authorizeRequests()


                .antMatchers("/oauth/token", "/h2-console/**").permitAll()
                .antMatchers(HttpMethod.POST, "/users/activate").permitAll()

                .antMatchers(HttpMethod.GET, "/users/me").authenticated()

                .antMatchers(HttpMethod.GET, "/users/**")
                .hasAnyAuthority(
                        "USER_READ",
                        "USER_WRITE",
                        "USER_DELETE",
                        "USER_STATUS_CHANGE"
                )
                .antMatchers(HttpMethod.POST, "/users/**").hasAuthority("USER_WRITE")
                .antMatchers(HttpMethod.PUT, "/users/**").hasAuthority("USER_WRITE")
                .antMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("USER_DELETE")
                .antMatchers(HttpMethod.PATCH, "/users/*/active").hasAuthority("USER_STATUS_CHANGE")

                .antMatchers(HttpMethod.GET, "/roles/**").hasAuthority("ROLE_READ")
                .antMatchers(HttpMethod.POST, "/roles/**").hasAuthority("ROLE_WRITE")
                .antMatchers(HttpMethod.PUT, "/roles/**").hasAuthority("ROLE_WRITE")

                .antMatchers(HttpMethod.GET, "/permissions/**").hasAuthority("PERMISSION_READ")

                .anyRequest().authenticated();

        http.csrf().disable();
    }


}
