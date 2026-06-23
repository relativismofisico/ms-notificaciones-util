package co.com.email.security.jwt;

import co.com.email.exception.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Filtro JWT que intercepta cada request, valida el Bearer token
 * y establece la autenticación en el SecurityContext.
 *
 * Flujo:
 * 1. Sin header Authorization → continúa sin autenticar (Spring Security devolverá 401)
 * 2. Token expirado → responde 401 directamente con code TOKEN_EXPIRED
 * 3. Token inválido (firma, formato) → responde 401 con code TOKEN_INVALID
 * 4. Token válido → extrae username y rol, establece Authentication
 *
 * No se registra automáticamente como servlet filter porque no tiene @Component;
 * se agrega explícitamente en SecurityConfig.addFilterBefore().
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROL_CLAIM = "rol";

    private final JwtValidator jwtValidator;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            Claims claims = jwtValidator.validateAndExtract(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = claims.getSubject();
                String rol = (String) claims.get(ROL_CLAIM);

                Collection<GrantedAuthority> authorities = JwtRoleConverter.convert(rol);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("[JwtAuthFilter] Autenticación establecida para usuario '{}' con rol '{}'", username, rol);
            }

        } catch (ExpiredJwtException ex) {
            log.warn("[JwtAuthFilter] Token expirado para request [{}] {}", request.getMethod(), request.getRequestURI());
            writeErrorResponse(response, request, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "El token JWT ha expirado");
            return;

        } catch (JwtException ex) {
            log.warn("[JwtAuthFilter] Token JWT inválido: {}", ex.getMessage());
            writeErrorResponse(response, request, HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "Token JWT inválido o malformado");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response,
                                    HttpServletRequest request,
                                    HttpStatus status,
                                    String code,
                                    String message) throws IOException {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .path(request.getRequestURI())
                .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
