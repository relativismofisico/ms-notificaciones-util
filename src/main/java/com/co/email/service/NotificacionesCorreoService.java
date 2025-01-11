package com.co.email.service;

import com.co.email.dto.CorreoSMTPRequestDto;

public interface NotificacionesCorreoService {

    //public void simpleSend(CorreoSMTPRequestDto correoSMTPRequestDto);
    public void htmlSend(CorreoSMTPRequestDto correoSMTPRequestDto);
}
