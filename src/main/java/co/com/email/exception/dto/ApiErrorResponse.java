package co.com.email.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estructura estándar corporativa de respuesta de error.
 * Todos los microservicios de la plataforma deben usar este contrato.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    /**
     * Momento en que ocurrió el error (UTC).
     */
    private final LocalDateTime timestamp;

    /**
     * Código HTTP de la respuesta (ej: 400, 422, 500).
     */
    private final int status;

    /**
     * Descripción estándar del código HTTP (ej: "Bad Request").
     */
    private final String error;

    /**
     * Código de error interno de la plataforma (ej: "EMAIL_VALIDATION_ERROR").
     * Facilita la trazabilidad entre microservicios.
     */
    private final String code;

    /**
     * Mensaje legible para el consumidor del API.
     */
    private final String message;

    /**
     * Path del endpoint que originó el error.
     */
    private final String path;

    /**
     * Lista de errores de validación de campos (solo presente en 400/422).
     */
    private final List<FieldError> fieldErrors;

    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String rejectedValue;
        private final String message;
    }
}
