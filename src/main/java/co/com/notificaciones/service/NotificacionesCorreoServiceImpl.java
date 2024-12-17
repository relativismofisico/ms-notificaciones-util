package co.com.notificaciones.service;

import org.springframework.beans.factory.annotation.Autowired;

import co.com.notificaciones.client.NotificaionCorreoSMTPClient;
import co.com.notificaciones.dto.CorreoSMTPRequestDto;

public class NotificacionesCorreoServiceImpl implements NotificacionesCorreoService{

	@Autowired
	private NotificaionCorreoSMTPClient notificaionCorreoSMTPClient;
	
	@Override
	public void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto) {
		// TODO Auto-generated method stub
		
	}

}
