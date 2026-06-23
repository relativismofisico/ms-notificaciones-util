package co.com.email.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Convierte el claim "rol" del JWT en GrantedAuthority de Spring Security.
 *
 * Convención corporativa: sin prefijo ROLE_. Las anotaciones @PreAuthorize
 * usan hasAuthority() (via roleEvaluator) en lugar de hasRole().
 *
 * El claim "rol" en ms-security es un String simple: "ADMINISTRADOR", "EMPRESA", etc.
 */
public final class JwtRoleConverter {

    private JwtRoleConverter() {
    }

    public static Collection<GrantedAuthority> convert(String rolClaim) {
        if (rolClaim == null || rolClaim.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(new SimpleGrantedAuthority(rolClaim));
    }
}
