package co.com.email.exception;

import co.com.email.exception.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Manejador global de excepciones para el microservicio.
 *
 * Convención corporativa de respuestas de error:
 * - 400 Bad Request      → falla de Bean Validation (@NotNull, @NotEmpty, etc.)
 * - 404 Not Found        → recurso no existe (ResourceNotFoundException)
 * - 422 Unprocessable    → la sintaxis es válida pero la semántica de negocio falla
 * - 500 Internal Error   → error no controlado
 *
 * Todos los errores usan ApiErrorResponse como cuerpo de respuesta.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionManager {

    // ── 400 Bad Request ────────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> ApiErrorResponse.FieldError.builder()
                        .field(fe.getField())
                        .rejectedValue(fe.getRejectedValue() != null ? fe.getRejectedValue().toString() : null)
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        log.warn("[ExceptionManager] 400 Bad Request en [{}] {}: {} campo(s) inválido(s)",
                request.getMethod(), sanitize(request.getRequestURI()), fieldErrors.size());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .code("VALIDATION_ERROR")
                .message("Uno o más parámetros no son válidos")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    // ── 404 Not Found ──────────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("[ExceptionManager] 404 Not Found en [{}] {}: {}",
                request.getMethod(), sanitize(request.getRequestURI()), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildBody(ex, request));
    }

    // ── 422 Unprocessable Entity ───────────────────────────────────────────────

    @ExceptionHandler(EmailValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailValidation(
            EmailValidationException ex,
            HttpServletRequest request) {

        log.warn("[ExceptionManager] 422 Unprocessable Entity en [{}] {}: {}",
                request.getMethod(), sanitize(request.getRequestURI()), ex.getMessage());

        return ResponseEntity.unprocessableEntity().body(buildBody(ex, request));
    }

    // ── Fallback BaseApiException ──────────────────────────────────────────────

    @ExceptionHandler(BaseApiException.class)
    public ResponseEntity<ApiErrorResponse> handleBaseApiException(
            BaseApiException ex,
            HttpServletRequest request) {

        log.error("[ExceptionManager] {} en [{}] {}: {}",
                ex.getHttpStatus().value(), request.getMethod(),
                sanitize(request.getRequestURI()), ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(buildBody(ex, request));
    }

    // ── 500 Internal Server Error ──────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("[ExceptionManager] 500 Internal Server Error en [{}] {}",
                request.getMethod(), sanitize(request.getRequestURI()), ex);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .code("INTERNAL_ERROR")
                .message("Error interno del servidor")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.internalServerError().body(body);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private ApiErrorResponse buildBody(BaseApiException ex, HttpServletRequest request) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .code(ex.getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    private String sanitize(String input) {
        return input != null ? input.replaceAll("[^a-zA-Z0-9._+/\\-]", "") : "";
    }
}
