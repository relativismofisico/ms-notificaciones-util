package co.com.email.service

import spock.lang.Specification
import spock.lang.Unroll

class UsuarioEmailResolverSpec extends Specification {

    UserEmailService userEmailService = Mock()
    UsuarioEmailResolver resolver = new UsuarioEmailResolver(userEmailService)

    // ── soporta ──────────────────────────────────────────────────────────────

    @Unroll
    def "soporta retorna true para tipoActor '#tipo'"() {
        expect:
        resolver.soporta(tipo)

        where:
        tipo << ["USUARIO", "OPERARIO", "FONDEADOR", "OTRO", "cualquier_cosa"]
    }

    def "soporta retorna false para tipoActor EMPRESA"() {
        expect:
        !resolver.soporta("EMPRESA")
    }

    def "soporta retorna false para tipoActor empresa en minúscula"() {
        expect:
        !resolver.soporta("empresa")
    }

    def "soporta retorna false para tipoActor Empresa mixto"() {
        expect:
        !resolver.soporta("Empresa")
    }

    // ── getEmail ──────────────────────────────────────────────────────────────

    def "getEmail delega en userEmailService y retorna email"() {
        when:
        def result = resolver.getEmail("jperez")

        then:
        1 * userEmailService.getEmailByUsername("jperez") >> "jperez@test.com"
        result == "jperez@test.com"
    }

    // ── getNombre ─────────────────────────────────────────────────────────────

    def "getNombre delega en userEmailService y retorna nombre completo"() {
        when:
        def result = resolver.getNombre("jperez")

        then:
        1 * userEmailService.getFullNameByUsername("jperez") >> "Juan Pérez"
        result == "Juan Pérez"
    }
}