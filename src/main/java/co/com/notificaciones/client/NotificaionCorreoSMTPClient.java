package co.com.notificaciones.client;

import co.com.notificaciones.dto.CorreoSMTPRequestDto;

public interface NotificaionCorreoSMTPClient {
	
	//Intenta enviar un correo electronico por protocolo SMTP
	public void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto);

}
