package co.com.email.exception

import org.springframework.http.HttpStatus
import spock.lang.Specification

class BaseApiExceptionSpec extends Specification {

    // Subclase estática concreta usada solo en tests — evita SE_INNER_CLASS de SpotBugs
    static class TestableException extends BaseApiException {
        TestableException(String message, HttpStatus httpStatus, String code, Throwable cause) {
            super(message, httpStatus, code, cause)
        }
    }

    def "constructor con mensaje, status y código inicializa campos correctamente"() {
        given:
        def ex = new EmailValidationException("Email inválido")

        expect:
        ex.message == "Email inválido"
        ex.httpStatus == HttpStatus.UNPROCESSABLE_ENTITY
        ex.code == "EMAIL_VALIDATION_ERROR"
    }

    def "constructor con causa encadena la excepción correctamente"() {
        given:
        def cause = new RuntimeException("causa original")
        def ex = new TestableException("mensaje", HttpStatus.INTERNAL_SERVER_ERROR, "TEST_CODE", cause)

        expect:
        ex.message == "mensaje"
        ex.httpStatus == HttpStatus.INTERNAL_SERVER_ERROR
        ex.code == "TEST_CODE"
        ex.cause == cause
    }

    def "ResourceNotFoundException constructor de un parámetro setea el mensaje"() {
        given:
        def ex = new ResourceNotFoundException("Template no encontrada")

        expect:
        ex.message == "Template no encontrada"
        ex.httpStatus == HttpStatus.NOT_FOUND
        ex.code == "RESOURCE_NOT_FOUND"
    }

    def "ResourceNotFoundException constructor de dos parámetros construye mensaje con tipo e identificador"() {
        given:
        def ex = new ResourceNotFoundException("Template", "otp-bienvenida")

        expect:
        ex.message == "Template no encontrado: otp-bienvenida"
        ex.httpStatus == HttpStatus.NOT_FOUND
    }

    def "EmailValidationException tiene constante CODE correcta"() {
        expect:
        EmailValidationException.CODE == "EMAIL_VALIDATION_ERROR"
    }

    def "ResourceNotFoundException tiene constante CODE correcta"() {
        expect:
        ResourceNotFoundException.CODE == "RESOURCE_NOT_FOUND"
    }
}