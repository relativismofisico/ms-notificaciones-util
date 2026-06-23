package co.com.email.security.config;

import co.com.email.security.handler.SecurityAccessDeniedHandler;
import co.com.email.security.handler.SecurityEntryPoint;
import co.com.email.security.jwt.JwtAuthFilter;
import co.com.email.security.jwt.JwtValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de Spring Security para el microservicio.
 *
 * Estrategia:
 * - Stateless: sin sesión HTTP (JWT por cada request)
 * - Sin CSRF (API REST)
 * - Todo endpoint requiere autenticación; la autorización por rol se
 *   delega a @PreAuthorize en cada controlador
 * - El filtro JWT se ejecuta antes del filtro de usuario/contraseña estándar
 *
 * Rutas públicas:
 * - /v3/api-docs/** y /swagger-ui/** (documentación API, restringir en prod si aplica)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityEntryPoint securityEntryPoint;
    private final SecurityAccessDeniedHandler accessDeniedHandler;
    private final JwtValidator jwtValidator;
    private final ObjectMapper objectMapper;

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtValidator, objectMapper);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}