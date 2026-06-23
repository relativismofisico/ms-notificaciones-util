package co.com.email.security.evaluator;

import co.com.email.security.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean reutilizable en expresiones SpEL de @PreAuthorize para
 * evaluar autorización basada en grupos de roles configurados en application.yml.
 *
 * Uso en controladores:
 *   @PreAuthorize("@roleEvaluator.hasAccess(authentication, 'administracion')")
 *
 * Cuando los roles permitidos cambian en el yml, no se requiere recompilar.
 * Solo se necesita reiniciar el microservicio.
 */
@Component("roleEvaluator")
@RequiredArgsConstructor
public class RoleEvaluator {

    private final SecurityProperties securityProperties;

    /**
     * @param authentication autenticación del contexto Spring Security (inyectada por SpEL)
     * @param group          clave del grupo en security.rol (ej: "administracion")
     * @return true si el usuario tiene alguno de los roles del grupo
     */
    public boolean hasAccess(Authentication authentication, String group) {
        List<String> allowedRoles = securityProperties.getRolesForGroup(group);

        return !allowedRoles.isEmpty() && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(allowedRoles::contains);
    }
}
