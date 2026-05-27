package com.electromart.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ElectroMart E-Commerce API",
                version = "1.0.0",
                description = "Complete API documentation for ElectroMart Electrical Items E-Commerce Platform",
                contact = @Contact(
                        name = "ElectroMart Support",
                        email = "support@electromart.com",
                        url = "https://electromart.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
            @Server(
                url = "http://localhost:8080",
                description = "Local Development Server"
            ),
            @Server(
                url = "https://api.electromart.com",
                description = "Production Server"
            )
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter JWT token: Bearer <token>"
)
public class SwaggerConfig {
}