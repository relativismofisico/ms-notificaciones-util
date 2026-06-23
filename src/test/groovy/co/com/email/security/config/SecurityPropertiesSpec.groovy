package co.com.email.security.config

import spock.lang.Specification

class SecurityPropertiesSpec extends Specification {

    SecurityProperties properties = new SecurityProperties()

    def "getRolesForGroup con grupo existente retorna lista de roles"() {
        given:
        properties.rol = [administracion: ["ADMINISTRADOR", "EMPRESA"]]

        when:
        def result = properties.getRolesForGroup("administracion")

        then:
        result == ["ADMINISTRADOR", "EMPRESA"]
    }

    def "getRolesForGroup con grupo inexistente retorna lista vacía"() {
        when:
        def result = properties.getRolesForGroup("grupo_inexistente")

        then:
        result != null
        result.isEmpty()
    }

    def "getRolesForGroup con mapa vacío retorna lista vacía"() {
        given:
        properties.rol = [:]

        when:
        def result = properties.getRolesForGroup("administracion")

        then:
        result != null
        result.isEmpty()
    }

    def "getRolesForGroup con grupo con un solo rol retorna lista con un elemento"() {
        given:
        properties.rol = [operador: ["OPERARIO"]]

        when:
        def result = properties.getRolesForGroup("operador")

        then:
        result == ["OPERARIO"]
    }

    def "jwt getter y setter funcionan correctamente"() {
        given:
        def jwt = new SecurityProperties.Jwt()
        jwt.secret = "mi-secreto"
        properties.jwt = jwt

        expect:
        properties.jwt.secret == "mi-secreto"
    }
}