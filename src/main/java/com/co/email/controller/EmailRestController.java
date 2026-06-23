package com.co.email.controller;

import com.co.email.dto.NotificationRequest;
import com.co.email.dto.CorreoSMTPRequestDto;
import com.co.email.service.NotificacionesCorreoService;
import com.co.email.service.NotificacionesCorreoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EmailRestController {

    @Autowired
    NotificacionesCorreoService notificacionesCorreoServiceImpl;

    /*@PostMapping("/sendNotification")
    public String sendNotification(@RequestBody NotificationRequest notificationRequest) {

        notificacionesCorreoServiceImpl.simpleSend(notificationRequest.getEmail(), notificationRequest.getSubject(), notificationRequest.getMessage());

        return "Message Queued";
    }*/

    @PostMapping("/sendHTMLEmail")
    public String sendHTMLEmail(@RequestBody CorreoSMTPRequestDto correoSMTPRequestDto) {

        notificacionesCorreoServiceImpl.htmlSend(correoSMTPRequestDto);

        return "Message Queued";
    }

}
