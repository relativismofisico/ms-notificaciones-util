package co.com.notificaciones.util;


import org.apache.commons.validator.EmailValidator;

public class ValidadorUtil {
	
	private ValidadorUtil() {
		
	}
	
	private static final EmailValidator emailValidator = EmailValidator.getInstance();
	
	public static boolean validateEmail(String email) {
		if(email == null || email.trim().equals("")) {
			return false;
		}
		
		return emailValidator.isValid(email.trim());
	}
	
	public static String canonicalize(String prevalidateStr) {
		try {
			if(prevalidateStr !=null) {
				return new String(prevalidateStr.getBytes("ISO-8859-1"), "UTF-8");
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
