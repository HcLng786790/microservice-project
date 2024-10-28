package com.huuduc.orderservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI productServiceAPI(){

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info().title("Order Service API")
                        .description("This is the REST API for Order Service")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to the Order Service Documentation")
                        .url("http://localhost:8060/api-docs"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
