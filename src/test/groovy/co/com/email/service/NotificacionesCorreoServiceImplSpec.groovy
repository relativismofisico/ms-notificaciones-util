package co.com.email.service

import co.com.email.dto.CorreoSMTPRequestDto
import co.com.email.exception.EmailValidationException
import co.com.email.validation.CorreoSMTPValidator
import spock.lang.Specification

class NotificacionesCorreoServiceImplSpec extends Specification {

    NotificaionCorreoSMTPClient notificacionCorreoSMTPClient = Mock()
    CorreoSMTPValidator correoSMTPValidator = Mock()
    NotificacionesCorreoServiceImpl service =
            new NotificacionesCorreoServiceImpl(notificacionCorreoSMTPClient, correoSMTPValidator)

    def "enviarCorreoSMTP ejecuta validación y llama al cliente SMTP"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("remitente@test.com")
                .nombreRemitente("Remitente")
                .asunto("Asunto")
                .destinatarios(["dest@test.com"])
                .build()

        when:
        service.enviarCorreoSMTP(dto)

        then:
        1 * correoSMTPValidator.validar(dto)
        1 * notificacionCorreoSMTPClient.enviarCorreoSMTP(dto)
    }

    def "enviarCorreoSMTP hace trim al nombreRemitente"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("r@test.com")
                .nombreRemitente("  Juan  ")
                .destinatarios(["d@test.com"])
                .build()

        when:
        service.enviarCorreoSMTP(dto)

        then:
        dto.nombreRemitente == "Juan"
        1 * notificacionCorreoSMTPClient.enviarCorreoSMTP(dto)
    }

    def "enviarCorreoSMTP establece nombreRemitente vacío cuando es null"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("r@test.com")
                .nombreRemitente(null)
                .destinatarios(["d@test.com"])
                .build()

        when:
        service.enviarCorreoSMTP(dto)

        then:
        dto.nombreRemitente == ""
        1 * notificacionCorreoSMTPClient.enviarCorreoSMTP(dto)
    }

    def "enviarCorreoSMTP prepara la firma antes de enviar"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("r@test.com")
                .nombreRemitente("R")
                .firma("Mi Firma")
                .destinatarios(["d@test.com"])
                .build()

        when:
        service.enviarCorreoSMTP(dto)

        then:
        dto.firma != null
        1 * notificacionCorreoSMTPClient.enviarCorreoSMTP(dto)
    }

    def "enviarCorreoSMTP propaga EmailValidationException desde el validador"() {
        given:
        def dto = CorreoSMTPRequestDto.builder()
                .correoRemitente("invalido")
                .destinatarios([])
                .build()
        correoSMTPValidator.validar(dto) >> { throw new EmailValidationException("Error de validación") }

        when:
        service.enviarCorreoSMTP(dto)

        then:
        thrown(EmailValidationException)
        0 * notificacionCorreoSMTPClient.enviarCorreoSMTP(_)
    }
}