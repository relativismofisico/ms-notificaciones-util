package co.com.email.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import spock.lang.Specification

class ExceptionManagerSpec extends Specification {

    ExceptionManager manager = new ExceptionManager()

    HttpServletRequest request = Mock() {
        getMethod() >> "POST"
        getRequestURI() >> "/api/test"
    }

    // ── 400 Bad Request ──────────────────────────────────────────────────────

    def "handleMethodArgumentNotValid retorna 400 con fieldErrors"() {
        given:
        FieldError fieldError = new FieldError("dto", "correoRemitente", "invalido", false, null, null, "formato inválido")
        BindingResult bindingResult = Mock()
        bindingResult.fieldErrors >> [fieldError]
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult)

        when:
        def response = manager.handleMethodArgumentNotValid(ex, request)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body.status == 400
        response.body.code == "VALIDATION_ERROR"
        response.body.fieldErrors.size() == 1
        response.body.fieldErrors[0].field == "correoRemitente"
    }

    def "handleMethodArgumentNotValid incluye rejectedValue null en fieldErrors"() {
        given:
        FieldError fieldError = new FieldError("dto", "campo", null, false, null, null, "campo requerido")
        BindingResult bindingResult = Mock()
        bindingResult.fieldErrors >> [fieldError]
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult)

        when:
        def response = manager.handleMethodArgumentNotValid(ex, request)

        then:
        response.body.fieldErrors[0].rejectedValue == null
    }

    // ── 404 Not Found ────────────────────────────────────────────────────────

    def "handleResourceNotFound retorna 404 con código RESOURCE_NOT_FOUND"() {
        given:
        def ex = new ResourceNotFoundException("Plantilla no encontrada")

        when:
        def response = manager.handleResourceNotFound(ex, request)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body.status == 404
        response.body.code == "RESOURCE_NOT_FOUND"
        response.body.message == "Plantilla no encontrada"
    }

    def "handleResourceNotFound construye mensaje para constructor de dos parámetros"() {
        given:
        def ex = new ResourceNotFoundException("Template", "otp-bienvenida")

        when:
        def response = manager.handleResourceNotFound(ex, request)

        then:
        response.body.message.contains("otp-bienvenida")
    }

    // ── 422 Unprocessable ────────────────────────────────────────────────────

    def "handleEmailValidation retorna 422 con código EMAIL_VALIDATION_ERROR"() {
        given:
        def ex = new EmailValidationException("Email inválido")

        when:
        def response = manager.handleEmailValidation(ex, request)

        then:
        response.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
        response.body.status == 422
        response.body.code == "EMAIL_VALIDATION_ERROR"
        response.body.message == "Email inválido"
    }

    // ── Fallback BaseApiException ────────────────────────────────────────────

    def "handleBaseApiException retorna status del HttpStatus de la excepción"() {
        given:
        def ex = new ResourceNotFoundException("recurso")

        when:
        def response = manager.handleBaseApiException(ex, request)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    // ── 500 Internal Error ───────────────────────────────────────────────────

    def "handleGenericException retorna 500 con código INTERNAL_ERROR"() {
        given:
        def ex = new RuntimeException("Error inesperado")

        when:
        def response = manager.handleGenericException(ex, request)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        response.body.status == 500
        response.body.code == "INTERNAL_ERROR"
        response.body.message == "Error interno del servidor"
    }

    def "handleGenericException no incluye fieldErrors"() {
        given:
        def ex = new NullPointerException("NPE")

        when:
        def response = manager.handleGenericException(ex, request)

        then:
        response.body.fieldErrors == null
    }

    // ── sanitize ─────────────────────────────────────────────────────────────

    def "sanitize afecta solo el log, el path en response usa requestURI original"() {
        given:
        def uri = "/api/notificarCorreo"
        HttpServletRequest req = Mock() {
            getMethod() >> "POST"
            getRequestURI() >> uri
        }
        def ex = new RuntimeException("error")

        when:
        def response = manager.handleGenericException(ex, req)

        then:
        response.body.path == uri
    }

    def "todos los handlers incluyen path, timestamp y error en el cuerpo"() {
        given:
        def ex = new EmailValidationException("error")

        when:
        def response = manager.handleEmailValidation(ex, request)

        then:
        response.body.path != null
        response.body.timestamp != null
        response.body.error != null
    }
}