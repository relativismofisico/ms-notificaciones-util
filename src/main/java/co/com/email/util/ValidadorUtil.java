package co.com.email.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.EmailValidator;

import java.nio.charset.StandardCharsets;

@Slf4j
public final class ValidadorUtil {

    private ValidadorUtil() {
    }

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    /**
     * Valida si el email tiene un formato correcto.
     *
     * @param email dirección de correo a validar
     * @return true si el email es válido
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_VALIDATOR.isValid(email.trim());
    }

    /**
     * Canonicaliza una cadena de texto de ISO-8859-1 a UTF-8.
     *
     * @param prevalidateStr cadena a canonicalizar
     * @return cadena en UTF-8 o null si hay error
     */
    public static String canonicalize(String prevalidateStr) {
        if (prevalidateStr == null) {
            return null;
        }
        try {
            return new String(prevalidateStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("[ValidadorUtil] Error al canonicalizar cadena: {}", e.getMessage());
            return null;
        }
    }
}
