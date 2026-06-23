package co.com.email.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepción base para todas las excepciones de negocio de la plataforma.
 * Cada microservicio debe extender esta clase para sus excepciones de dominio.
 *
 * Convención de códigos internos: SCREAMING_SNAKE_CASE, prefijo por dominio.
 * Ejemplos: EMAIL_VALIDATION_ERROR, TEMPLATE_NOT_FOUND, USER_NOT_FOUND.
 */
@Getter
public abstract class BaseApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;

    protected BaseApiException(String message, HttpStatus httpStatus, String code) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    protected BaseApiException(String message, HttpStatus httpStatus, String code, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
    }
}