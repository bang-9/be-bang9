package me.bang9.api.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI bang9OpenApi() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://dev.bang9.me")
                                .description("Development server"),
                        new Server()
                                .url("https://api.bang9.me")
                                .description("Production server")
                ));
//                .components(new Components()
//                        .addSecuritySchemes("bearer-jwt", securityScheme())
//                )
//                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Bang9 API")
                .description("Bang9 API Documentation")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Bang9 Development Team")
                        .email("contact@bang9.me")
                        .url("https://github.com/bang-9")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                );
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Authentication");
    }
}