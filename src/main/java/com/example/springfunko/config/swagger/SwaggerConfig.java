package com.example.springfunko.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API REST Funko Pop!")
                                .version("1.0.0")
                                .description("API REST de ejemplo para la asignatura de Desarrollo Web en Entornos Servidor")

                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("GitHub del Proyecto")
                                .url("https://github.com/jaimemen897/FunkoSpring.git")
                )
                .addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                ("Bearer Authentication", createAPIKeyScheme()));
    }


    @Bean
    GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("https")
                .pathsToMatch("/api/funkos/**", "/api/auth/signin")
                .displayName("API Tienda Spring Boot DAW 2023/2024")
                .build();
    }
}