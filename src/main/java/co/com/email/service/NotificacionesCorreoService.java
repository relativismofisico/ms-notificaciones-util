package co.com.email.service;

import co.com.email.dto.CorreoSMTPRequestDto;

public interface NotificacionesCorreoService {

	void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto);
}
