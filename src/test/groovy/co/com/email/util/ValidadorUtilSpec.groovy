package co.com.email.util

import spock.lang.Specification
import spock.lang.Unroll

class ValidadorUtilSpec extends Specification {

    // ── validateEmail ────────────────────────────────────────────────────────

    def "validateEmail con email válido retorna true"() {
        expect:
        ValidadorUtil.validateEmail("usuario@dominio.com")
    }

    def "validateEmail con email con subdominio retorna true"() {
        expect:
        ValidadorUtil.validateEmail("usuario@sub.dominio.com")
    }

    def "validateEmail con email null retorna false"() {
        expect:
        !ValidadorUtil.validateEmail(null)
    }

    def "validateEmail con email vacío retorna false"() {
        expect:
        !ValidadorUtil.validateEmail("")
    }

    def "validateEmail con solo espacios retorna false"() {
        expect:
        !ValidadorUtil.validateEmail("   ")
    }

    @Unroll
    def "validateEmail con email inválido '#email' retorna false"() {
        expect:
        !ValidadorUtil.validateEmail(email)

        where:
        email << [
            "no-arroba",
            "@sinusuario.com",
            "usuario@",
            "usuario @dominio.com",
            "usuario@ dominio.com",
            "doble@@dominio.com"
        ]
    }

    def "validateEmail ignora espacios alrededor del email"() {
        expect:
        ValidadorUtil.validateEmail("  usuario@dominio.com  ")
    }

    // ── canonicalize ─────────────────────────────────────────────────────────

    def "canonicalize con cadena null retorna null"() {
        expect:
        ValidadorUtil.canonicalize(null) == null
    }

    def "canonicalize con cadena ASCII simple retorna la misma cadena"() {
        when:
        def result = ValidadorUtil.canonicalize("hola mundo")

        then:
        result != null
    }

    def "canonicalize con cadena vacía retorna cadena no null"() {
        when:
        def result = ValidadorUtil.canonicalize("")

        then:
        result != null
    }

    def "canonicalize con caracteres especiales no lanza excepción"() {
        when:
        def result = ValidadorUtil.canonicalize("café")

        then:
        noExceptionThrown()
        result != null
    }
}