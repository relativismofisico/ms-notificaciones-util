package co.com.email.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Propiedades de seguridad corporativas leídas desde application.yml.
 *
 * Estructura esperada:
 * security:
 *   jwt:
 *     secret: <base64-encoded-secret>
 *   rol:
 *     administracion:
 *       - ADMINISTRADOR
 *       - EMPRESA
 */
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private Jwt jwt = new Jwt();
    private Map<String, List<String>> rol = new HashMap<>();

    @Data
    public static class Jwt {
        private String secret;
    }

    public List<String> getRolesForGroup(String group) {
        return rol.getOrDefault(group, new ArrayList<>());
    }
}