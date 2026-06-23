package co.com.email.security.evaluator

import co.com.email.security.config.SecurityProperties
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import spock.lang.Specification

class RoleEvaluatorSpec extends Specification {

    SecurityProperties securityProperties = Mock()
    RoleEvaluator roleEvaluator = new RoleEvaluator(securityProperties)

    def "hasAccess retorna true cuando usuario tiene un rol del grupo"() {
        given:
        Authentication auth = Mock()
        auth.authorities >> [new SimpleGrantedAuthority("ADMINISTRADOR")]
        securityProperties.getRolesForGroup("administracion") >> ["ADMINISTRADOR", "EMPRESA"]

        when:
        def result = roleEvaluator.hasAccess(auth, "administracion")

        then:
        result
    }

    def "hasAccess retorna false cuando usuario no tiene ningún rol del grupo"() {
        given:
        Authentication auth = Mock()
        auth.authorities >> [new SimpleGrantedAuthority("INVITADO")]
        securityProperties.getRolesForGroup("administracion") >> ["ADMINISTRADOR", "EMPRESA"]

        when:
        def result = roleEvaluator.hasAccess(auth, "administracion")

        then:
        !result
    }

    def "hasAccess retorna false cuando lista de roles del grupo está vacía"() {
        given:
        Authentication auth = Mock()
        auth.authorities >> [new SimpleGrantedAuthority("ADMINISTRADOR")]
        securityProperties.getRolesForGroup("vacio") >> []

        when:
        def result = roleEvaluator.hasAccess(auth, "vacio")

        then:
        !result
    }

    def "hasAccess retorna false cuando usuario no tiene autoridades"() {
        given:
        Authentication auth = Mock()
        auth.authorities >> []
        securityProperties.getRolesForGroup("administracion") >> ["ADMINISTRADOR"]

        when:
        def result = roleEvaluator.hasAccess(auth, "administracion")

        then:
        !result
    }

    def "hasAccess retorna true con rol EMPRESA en grupo administracion"() {
        given:
        Authentication auth = Mock()
        auth.authorities >> [new SimpleGrantedAuthority("EMPRESA")]
        securityProperties.getRolesForGroup("administracion") >> ["ADMINISTRADOR", "EMPRESA", "OPERARIO"]

        when:
        def result = roleEvaluator.hasAccess(auth, "administracion")

        then:
        result
    }
}