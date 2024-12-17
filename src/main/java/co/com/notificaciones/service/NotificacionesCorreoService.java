package co.com.notificaciones.service;

import co.com.notificaciones.dto.CorreoSMTPRequestDto;

public interface NotificacionesCorreoService {

	void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto);
}
