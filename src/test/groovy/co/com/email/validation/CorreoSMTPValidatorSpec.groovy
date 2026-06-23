package co.com.email.validation

import co.com.email.dto.CorreoSMTPRequestDto
import co.com.email.exception.EmailValidationException
import spock.lang.Specification

class CorreoSMTPValidatorSpec extends Specification {

    CorreoSMTPValidator validator = new CorreoSMTPValidator(10)

    // ── remitente inválido ───────────────────────────────────────────────────

    def "validar lanza excepción cuando correoRemitente es null"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente(null)
                .destinatarios(["dest@test.com"])
                .build()

        when:
        validator.validar(dto)

        then:
        thrown(EmailValidationException)
    }

    def "validar lanza excepción cuando correoRemitente tiene formato inválido"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("no-es-email")
                .destinatarios(["dest@test.com"])
                .build()

        when:
        validator.validar(dto)

        then:
        thrown(EmailValidationException)
    }

    // ── firma nula/vacía ────────────────────────────────────────────────────

    def "validar registra advertencia cuando firma es null y continúa"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .firma(null)
                .destinatarios(["dest@test.com"])
                .build()

        when:
        validator.validar(dto)

        then:
        noExceptionThrown()
    }

    def "validar registra advertencia cuando firma está en blanco y continúa"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .firma("   ")
                .destinatarios(["dest@test.com"])
                .build()

        when:
        validator.validar(dto)

        then:
        noExceptionThrown()
    }

    // ── destinatarios ────────────────────────────────────────────────────────

    def "validar filtra destinatarios inválidos y conserva los válidos"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(["valido@test.com", "invalido", "otro@test.com"])
                .build()

        when:
        validator.validar(dto)

        then:
        dto.destinatarios == ["valido@test.com", "otro@test.com"]
    }

    def "validar lanza excepción cuando todos los destinatarios son inválidos"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(["invalido1", "invalido2"])
                .build()

        when:
        validator.validar(dto)

        then:
        thrown(EmailValidationException)
    }

    def "validar lanza excepción cuando destinatarios es null y no hay cc ni bcc"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(null)
                .build()

        when:
        validator.validar(dto)

        then:
        thrown(EmailValidationException)
    }

    def "validar acepta destinatarios null cuando hay cc válido"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(null)
                .destinatariosCc(["cc@test.com"])
                .build()

        when:
        validator.validar(dto)

        then:
        noExceptionThrown()
    }

    def "validar filtra destinatariosCc inválidos"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(["dest@test.com"])
                .destinatariosCc(["cc-valido@test.com", "cc-invalido"])
                .build()

        when:
        validator.validar(dto)

        then:
        dto.destinatariosCc == ["cc-valido@test.com"]
    }

    def "validar filtra destinatariosBcc inválidos"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(["dest@test.com"])
                .destinatariosBcc(["bcc@test.com", "no-email"])
                .build()

        when:
        validator.validar(dto)

        then:
        dto.destinatariosBcc == ["bcc@test.com"]
    }

    def "validar cuando destinatariosCc es null no lanza excepción"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(["dest@test.com"])
                .destinatariosCc(null)
                .build()

        when:
        validator.validar(dto)

        then:
        noExceptionThrown()
    }

    def "validar cuando destinatariosBcc es null no lanza excepción"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(["dest@test.com"])
                .destinatariosBcc(null)
                .build()

        when:
        validator.validar(dto)

        then:
        noExceptionThrown()
    }

    // ── límite de destinatarios ──────────────────────────────────────────────

    def "validar registra advertencia cuando se excede límite por campo To"() {
        given:
        List<String> muchos = (1..11).collect { int i -> "user" + i + "@test.com" }
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(muchos)
                .build()

        when:
        validator.validar(dto)

        then:
        noExceptionThrown()
    }

    def "validar registra advertencia cuando total excede límite doble"() {
        given:
        List<String> muchos = (1..11).collect { int i -> "user" + i + "@test.com" }
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .destinatarios(muchos)
                .destinatariosCc(muchos)
                .build()

        when:
        validator.validar(dto)

        then:
        noExceptionThrown()
    }

    def "validar con request completo y válido no lanza excepción"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .nombreRemitente("Remitente Test")
                .asunto("Asunto de prueba")
                .firma("Firma válida")
                .destinatarios(["dest1@test.com", "dest2@test.com"])
                .destinatariosCc(["cc@test.com"])
                .destinatariosBcc(["bcc@test.com"])
                .build()

        when:
        validator.validar(dto)

        then:
        noExceptionThrown()
    }
}