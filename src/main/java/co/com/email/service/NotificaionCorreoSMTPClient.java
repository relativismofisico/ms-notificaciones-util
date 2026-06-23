package co.com.email.service;

import co.com.email.dto.CorreoSMTPRequestDto;

public interface NotificaionCorreoSMTPClient {

    void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto);
}
