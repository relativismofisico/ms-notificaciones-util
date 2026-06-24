package co.com.email.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Respuesta de error estándar de la plataforma Factoring")
public class ApiErrorResponse {

    @Schema(description = "Momento en que ocurrió el error (UTC)", example = "2024-01-15T10:30:00")
    private final LocalDateTime timestamp;

    @Schema(description = "Código HTTP de la respuesta", example = "400")
    private final int status;

    @Schema(description = "Descripción estándar del código HTTP", example = "Bad Request")
    private final String error;

    @Schema(
            description = "Código de error interno de la plataforma para trazabilidad",
            example = "VALIDATION_ERROR",
            allowableValues = {
                    "VALIDATION_ERROR",
                    "TOKEN_EXPIRED",
                    "TOKEN_INVALID",
                    "FORBIDDEN",
                    "RESOURCE_NOT_FOUND",
                    "EMAIL_VALIDATION_ERROR",
                    "INTERNAL_ERROR"
            }
    )
    private final String code;

    @Schema(description = "Mensaje legible para el consumidor del API", example = "Uno o más parámetros no son válidos")
    private final String message;

    @Schema(description = "Path del endpoint que originó el error", example = "/notificarCorreo")
    private final String path;

    @Schema(description = "Lista de errores de validación de campos. Solo presente en respuestas 400.")
    private final List<FieldError> fieldErrors;

    @Getter
    @Builder
    @Schema(description = "Detalle de un campo con error de validación")
    public static class FieldError {

        @Schema(description = "Nombre del campo con error", example = "correoRemitente")
        private final String field;

        @Schema(description = "Valor rechazado", example = "correo-invalido")
        private final String rejectedValue;

        @Schema(description = "Mensaje de validación", example = "no debe estar vacío")
        private final String message;
    }
}
