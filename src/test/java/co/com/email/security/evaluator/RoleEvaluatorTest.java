package co.com.email.security.evaluator;

import co.com.email.security.config.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoleEvaluatorTest {

    private RoleEvaluator roleEvaluator;

    @BeforeEach
    void setUp() {
        SecurityProperties properties = new SecurityProperties();
        properties.getRol().put("administracion",
                List.of("ADMINISTRADOR", "EMPRESA", "OPERARIO", "FONDEADOR"));
        roleEvaluator = new RoleEvaluator(properties);
    }

    @Test
    void debePermitirAccesoAAdministrador() {
        assertThat(roleEvaluator.hasAccess(auth("ADMINISTRADOR"), "administracion")).isTrue();
    }

    @Test
    void debePermitirAccesoAEmpresa() {
        assertThat(roleEvaluator.hasAccess(auth("EMPRESA"), "administracion")).isTrue();
    }

    @Test
    void debePermitirAccesoAOperario() {
        assertThat(roleEvaluator.hasAccess(auth("OPERARIO"), "administracion")).isTrue();
    }

    @Test
    void debePermitirAccesoAFondeador() {
        assertThat(roleEvaluator.hasAccess(auth("FONDEADOR"), "administracion")).isTrue();
    }

    @Test
    void debeDenegarAccesoARolNoConfigurado() {
        assertThat(roleEvaluator.hasAccess(auth("ROL_DESCONOCIDO"), "administracion")).isFalse();
    }

    @Test
    void debeDenegarAccesoAGrupoInexistente() {
        assertThat(roleEvaluator.hasAccess(auth("ADMINISTRADOR"), "grupo_inexistente")).isFalse();
    }

    @Test
    void debeDenegarAccesoSiRolEsNuloEnAuthority() {
        Authentication authSinRol = new UsernamePasswordAuthenticationToken("usuario", null, List.of());
        assertThat(roleEvaluator.hasAccess(authSinRol, "administracion")).isFalse();
    }

    private Authentication auth(String rol) {
        return new UsernamePasswordAuthenticationToken(
                "usuario", null, List.of(new SimpleGrantedAuthority(rol)));
    }
}