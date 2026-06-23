package co.com.email.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando los datos del correo no cumplen las reglas de negocio:
 * email remitente malformado, sin destinatarios válidos, etc.
 *
 * Mapea a HTTP 422 Unprocessable Entity porque la sintaxis del request es
 * válida (superó Bean Validation) pero la semántica de negocio falla.
 */
public class EmailValidationException extends BaseApiException {

    public static final String CODE = "EMAIL_VALIDATION_ERROR";

    public EmailValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, CODE);
    }
}