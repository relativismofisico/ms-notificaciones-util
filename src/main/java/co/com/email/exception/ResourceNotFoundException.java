package co.com.email.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando un recurso requerido no existe en el sistema
 * (ej: plantilla de email no encontrada, usuario no encontrado).
 *
 * Mapea a HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends BaseApiException {

    public static final String CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, CODE);
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(resourceType + " no encontrado: " + identifier, HttpStatus.NOT_FOUND, CODE);
    }
}