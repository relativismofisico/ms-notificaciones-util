package com.co.kafka.services;

import com.co.email.dto.CorreoSMTPRequestDto;
import com.co.email.dto.VariablesCorreoDTO;
import com.co.email.service.NotificacionesCorreoService;
import com.co.templates.entities.MessagesTemplates;
import com.co.templates.services.IMessagesTemplatesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final IMessagesTemplatesService messagesTemplatesService;
    private final NotificacionesCorreoService notificacionesCorreoService;

    @KafkaListener(topics = "emails", groupId = "companyGroup")
    private void consumeKafkaEmails(String data){

        CorreoSMTPRequestDto correoSMTPRequestDto = new Gson().fromJson(data, CorreoSMTPRequestDto.class);
        prepareEmailTemplate(correoSMTPRequestDto);
    }

    private void prepareEmailTemplate(CorreoSMTPRequestDto correoSMTPRequestDto){

        if (correoSMTPRequestDto.getTipoCorreo() == null){
            log.error("[KafkaConsumerService][prepareEmailTemplate][ms-notificaciones-util]\" + \" No hay tipo de correo especificado");
            throw new RuntimeException("No hay tipo de correo especificado");
        }

        Optional<MessagesTemplates> template = messagesTemplatesService.findById(correoSMTPRequestDto.getTipoCorreo());

        if (!template.isPresent()){
            log.error("[KafkaConsumerService][prepareEmailTemplate][ms-notificaciones-util]\" + \" La plantilla del correo no se encuentra en la BD");
            throw new RuntimeException("La plantilla del correo no se encuentra en la BD");
        }

        correoSMTPRequestDto.setAsunto(template.get().getEmailSubject());
        correoSMTPRequestDto.setCuerpoHtml(template.get().getContent());
        correoSMTPRequestDto.setCorreoRemitente("relativismofisico@gmail.com");
        correoSMTPRequestDto.setFirma("Atentamente, Nombre de la Empresa");
        correoSMTPRequestDto.setNombreRemitente("Plataforma Finamco");

        notificacionesCorreoService.htmlSend(correoSMTPRequestDto);
    }
}
