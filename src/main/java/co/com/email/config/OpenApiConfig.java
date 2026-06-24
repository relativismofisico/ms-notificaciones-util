package co.com.email.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Value("${spring.application.name:ms-notificaciones-util}")
    private String appName;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(buildInfo())
                .servers(buildServers())
                .components(buildComponents())
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }

    private Info buildInfo() {
        return new Info()
                .title("ms-notificaciones-util")
                .description("""
                        Microservicio de notificaciones para la plataforma Factoring.

                        Permite el envío de correos electrónicos vía SMTP y gestiona
                        notificaciones mediante consumo de eventos Kafka.

                        **Autenticación:** Bearer JWT (emitido por ms-security).

                        **Autorización:** Los endpoints requieren el rol `administracion`,
                        asignado a: ADMINISTRADOR, EMPRESA, OPERARIO, FONDEADOR.
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Equipo Factoring")
                        .email("relativismofisico@gmail.com"))
                .license(new License()
                        .name("Uso interno - Factoring")
                        .url("https://factoring.co.com"));
    }

    private List<Server> buildServers() {
        return List.of(
                new Server().url("http://localhost:8055/securityapi").description("Local"),
                new Server().url("http://dev.factoring.co.com/ms-notificacionescorreo-util").description("Desarrollo"),
                new Server().url("http://qa.factoring.co.com/ms-notificacionescorreo-util").description("QA"),
                new Server().url("https://factoring.co.com/ms-notificacionescorreo-util").description("Producción")
        );
    }

    private Components buildComponents() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                        Token JWT emitido por ms-security.

                        Incluir en el header: `Authorization: Bearer <token>`

                        El token debe contener el claim `rol` con uno de los valores:
                        ADMINISTRADOR, EMPRESA, OPERARIO, FONDEADOR.
                        """);

        return new Components()
                .addSecuritySchemes(BEARER_SCHEME, bearerScheme);
    }
}
