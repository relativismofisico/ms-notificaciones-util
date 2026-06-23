package co.com.email.service

import spock.lang.Specification
import spock.lang.Unroll

class EmpresaEmailResolverSpec extends Specification {

    CompanyEmailService companyEmailService = Mock()
    EmpresaEmailResolver resolver = new EmpresaEmailResolver(companyEmailService)

    // ── soporta ──────────────────────────────────────────────────────────────

    def "soporta retorna true para tipoActor EMPRESA"() {
        expect:
        resolver.soporta("EMPRESA")
    }

    def "soporta retorna true para tipoActor empresa en minúscula"() {
        expect:
        resolver.soporta("empresa")
    }

    def "soporta retorna true para tipoActor Empresa mixto"() {
        expect:
        resolver.soporta("Empresa")
    }

    @Unroll
    def "soporta retorna false para tipoActor '#tipo'"() {
        expect:
        !resolver.soporta(tipo)

        where:
        tipo << ["USUARIO", "OPERARIO", "FONDEADOR", "ADMINISTRADOR", "otro"]
    }

    // ── getEmail ──────────────────────────────────────────────────────────────

    def "getEmail delega en companyEmailService y retorna email"() {
        when:
        def result = resolver.getEmail("123456")

        then:
        1 * companyEmailService.getEmailByNit("123456") >> "empresa@test.com"
        result == "empresa@test.com"
    }

    // ── getNombre ─────────────────────────────────────────────────────────────

    def "getNombre delega en companyEmailService y retorna nombre"() {
        when:
        def result = resolver.getNombre("123456")

        then:
        1 * companyEmailService.getNombreByNit("123456") >> "Mi Empresa SA"
        result == "Mi Empresa SA"
    }
}