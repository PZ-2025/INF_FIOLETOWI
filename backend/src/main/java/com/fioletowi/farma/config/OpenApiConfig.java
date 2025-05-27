package com.fioletowi.farma.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Configuration class for OpenAPI documentation and security scheme setup.
 * <p>
 * Defines general API metadata and the JWT bearer authentication scheme for Swagger/OpenAPI.
 * </p>
 */
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(name = "Burglak"),
                description = "OpenAPI documentation for Spring Security",
                title = "OpenAPI Specification",
                version = "1.0"
        ),
        servers = {
                @Server(description = "Local ENV", url = "http://localhost:8080/api")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
