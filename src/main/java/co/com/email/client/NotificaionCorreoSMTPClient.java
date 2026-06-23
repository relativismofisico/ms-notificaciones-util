package co.com.email.client;

import co.com.email.dto.CorreoSMTPRequestDto;

public interface NotificaionCorreoSMTPClient {
	/**
	 * Intenta enviar un correo electronico por protocolo SMTP
	 */
	void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto);

}
