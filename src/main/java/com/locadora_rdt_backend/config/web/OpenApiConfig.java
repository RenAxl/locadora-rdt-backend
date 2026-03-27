package com.locadora_rdt_backend.config.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Locadora RDT API")
                        .version("v1")
                        .description("Documentação da API de clientes da Locadora RDT")
                        .contact(new Contact()
                                .name("Renan Duarte")
                                .email("seu-email@exemplo.com"))
                        .license(new License()
                                .name("Uso interno")));
    }
}