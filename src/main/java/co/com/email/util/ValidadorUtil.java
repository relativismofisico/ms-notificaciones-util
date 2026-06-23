package co.com.email.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.EmailValidator;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ValidadorUtil {

	private ValidadorUtil() {
	}

	private static final EmailValidator emailValidator = EmailValidator.getInstance();

	public static boolean validateEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		return emailValidator.isValid(email.trim());
	}

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
