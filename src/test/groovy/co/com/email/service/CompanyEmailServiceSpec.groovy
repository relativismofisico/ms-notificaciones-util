package co.com.email.service

import co.com.email.domain.entities.Company
import co.com.email.repositories.CompanyRepository
import spock.lang.Specification

class CompanyEmailServiceSpec extends Specification {

    CompanyRepository companyRepository = Mock()
    CompanyEmailService service = new CompanyEmailService(companyRepository)

    def "getEmailByNit retorna email cuando empresa existe"() {
        given:
        def company = Company.builder().nit("123456").emailCompany("empresa@test.com").build()
        companyRepository.findByNit("123456") >> Optional.of(company)

        when:
        def result = service.getEmailByNit("123456")

        then:
        result == "empresa@test.com"
    }

    def "getEmailByNit lanza RuntimeException cuando empresa no existe"() {
        given:
        companyRepository.findByNit("nit-no-existe") >> Optional.empty()

        when:
        service.getEmailByNit("nit-no-existe")

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("nit-no-existe")
    }

    def "getNombreByNit retorna nombre cuando empresa existe"() {
        given:
        def company = Company.builder().nit("123456").nameCompany("Mi Empresa SA").build()
        companyRepository.findByNit("123456") >> Optional.of(company)

        when:
        def result = service.getNombreByNit("123456")

        then:
        result == "Mi Empresa SA"
    }

    def "getNombreByNit retorna EMPRESA cuando empresa no existe"() {
        given:
        companyRepository.findByNit("nit-no-existe") >> Optional.empty()

        when:
        def result = service.getNombreByNit("nit-no-existe")

        then:
        result == "EMPRESA"
    }
}