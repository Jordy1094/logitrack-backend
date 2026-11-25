package com.transrapido.logitrack.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Documentacion de la API con Swagger / OpenAPI (Capitulo 7, RNF-10).
// Declara el esquema "bearerAuth" para que en la pagina de Swagger se pueda pegar
// el token JWT con el boton "Authorize" y probar los endpoints protegidos.
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_JWT = "bearerAuth";

    @Bean
    public OpenAPI logitrackOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiTrack API")
                        .version("1.0.0")
                        .description("API REST de LogiTrack: gestion de flota, asignacion de rutas y control de "
                                + "mantenimiento para TransRapido. Inicie sesion en POST /api/auth/login, copie el "
                                + "token y pulse 'Authorize' para probar los demas endpoints.")
                        .contact(new Contact().name("Grupo 04 - Soluciones Web y Aplicaciones Distribuidas")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT))
                .components(new Components().addSecuritySchemes(ESQUEMA_JWT,
                        new SecurityScheme()
                                .name(ESQUEMA_JWT)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
