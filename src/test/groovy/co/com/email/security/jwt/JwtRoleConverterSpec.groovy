package co.com.email.security.jwt

import spock.lang.Specification

class JwtRoleConverterSpec extends Specification {

    def "convert con rol válido retorna colección con una autoridad"() {
        when:
        def result = JwtRoleConverter.convert("ADMINISTRADOR")

        then:
        result.size() == 1
        result.first().authority == "ADMINISTRADOR"
    }

    def "convert con rol null retorna colección vacía"() {
        when:
        def result = JwtRoleConverter.convert(null)

        then:
        result.isEmpty()
    }

    def "convert con rol en blanco retorna colección vacía"() {
        when:
        def result = JwtRoleConverter.convert("")

        then:
        result.isEmpty()
    }

    def "convert con rol solo espacios retorna colección vacía"() {
        when:
        def result = JwtRoleConverter.convert("   ")

        then:
        result.isEmpty()
    }

    def "convert con rol EMPRESA retorna autoridad EMPRESA"() {
        when:
        def result = JwtRoleConverter.convert("EMPRESA")

        then:
        result.size() == 1
        result.first().authority == "EMPRESA"
    }

    def "convert con rol OPERARIO retorna autoridad OPERARIO"() {
        when:
        def result = JwtRoleConverter.convert("OPERARIO")

        then:
        result.size() == 1
        result.first().authority == "OPERARIO"
    }
}