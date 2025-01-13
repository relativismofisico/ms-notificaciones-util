package com.co.email.service;

import com.co.email.constantes.Constantes;
import com.co.email.dto.AdjuntoDto;
import com.co.email.dto.CorreoSMTPRequestDto;
import com.co.email.util.TextosUtil;
import com.co.email.util.ValidadorUtil;
import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class NotificacionesCorreoServiceImpl implements NotificacionesCorreoService {

    @Autowired(required = false)
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${cantidadLimiteDestinatarios}")
    private int cantidadLimiteDestinatarios;

    @Async
    @Override
    public void htmlSend(CorreoSMTPRequestDto correoSMTPRequestDto) {

        log.info("[EmailService][htmlSend] Inicio enviarCorreoSMTP: " + correoSMTPRequestDto.toString());

        validaCorreoSMTP(correoSMTPRequestDto);

        if (correoSMTPRequestDto.getNombreRemitente() == null) {
            correoSMTPRequestDto.setNombreRemitente("");
        } else {
            correoSMTPRequestDto.setNombreRemitente(correoSMTPRequestDto.getNombreRemitente().trim());
        }

        try {
            //Prepara la firma del correo
            correoSMTPRequestDto.setFirma(TextosUtil.prepararFirmaCorreo(correoSMTPRequestDto.getFirma()));

            //Crea el mensaje MIME
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            //Configura el remitente
            helper.setFrom(correoSMTPRequestDto.getCorreoRemitente(),correoSMTPRequestDto.getNombreRemitente());

            // Convertir la lista de destinatarios a un array y asignarla
            List<String> destinatariosList = correoSMTPRequestDto.getDestinatarios();
            if (destinatariosList == null || destinatariosList.isEmpty()) {
                throw new IllegalArgumentException("La lista de destinatarios no puede estar vacía");
            }
            String[] destinatariosArray = destinatariosList.toArray(new String[0]);
            helper.setTo(destinatariosArray);

            //Agrega Destinatarios con Copia
            List<String> destinatariosListCc = correoSMTPRequestDto.getDestinatariosCc();
            if (destinatariosListCc != null) {
                String[] destinatariosArrayCc = destinatariosListCc.toArray(new String[0]);
                helper.addCc(Arrays.toString(destinatariosArrayCc));
            }

            //Agrega Destinatarios con Copia Oculta
            List<String> destinatariosListBcc = correoSMTPRequestDto.getDestinatariosBcc();
            if (destinatariosListBcc != null) {
                String[] destinatariosArrayBcc = destinatariosListBcc.toArray(new String[0]);
                helper.addBcc(Arrays.toString(destinatariosArrayBcc));
            }

            //configura el asunto
            helper.setSubject(correoSMTPRequestDto.getAsunto());

            //Agrega Destinatarios con Copia Oculta
            List<AdjuntoDto> adjuntos = correoSMTPRequestDto.getAdjuntos();
            if (adjuntos != null && !adjuntos.isEmpty()) {
                for (AdjuntoDto adjunto : adjuntos) {
                    try {
                        helper.addAttachment(adjunto.getNombreArchivo(), (DataSource) adjunto);
                    }catch (Exception e){
                        log.warn("El archivo adjunto '{}' no existe o no es válido", adjunto);
                    }
                }
            }

            // Configurar el contenido del correo utilizando Thymeleaf
            Context context = new Context();

            // Properties to show up in Template after stored in Context
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("name", correoSMTPRequestDto.getNombreRemitente());
            properties.put("offerings", correoSMTPRequestDto.getOfferings());

            context.setVariables(properties);

            String html = templateEngine.process("emails/" + correoSMTPRequestDto.getCuerpoHtml(), context);


            helper.setText(html, true);

            log.info(html);

            //Envia el correo
            emailSender.send(message);
            log.info("simpleSend: Email Queued");
            log.info("htmlSend: Email enviado correctamente a los destinatarios: {}", destinatariosList);


        }
        catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }

    }

    /**
     * Metodo que que valida los datos a enviar en el correo via SMTP
     *
     */
    private void validaCorreoSMTP(CorreoSMTPRequestDto requestDto) {

        List<String> empty = new ArrayList<>();
        if (!ValidadorUtil.validateEmail(requestDto.getCorreoRemitente())) {
            log.info("Parámetro correoRemitente not a well-formed email address");
            throw new RuntimeException(Constantes.COD_VALIDACION_PARAMETROS_NO_VALIDOS);
        }

        if (requestDto.getFirma() == null || requestDto.getFirma().trim().isEmpty()) {
            log.warn("[NotificacionesCorreoServiceImpl][validaCorreoSMTP]La firma es nula o vacia");
        }

        Map<Boolean, List<String>> mails;
        if (nonNull(requestDto.getDestinatarios())) {
            mails = requestDto.getDestinatarios().stream().collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
            mails.getOrDefault(Boolean.FALSE, empty).forEach(x -> log.warn("[NotificacionesCorreoServiceImpl][validaCorreoSMTP] El destinatario \"{}\" no cumple con el formato de correo ", x));
            requestDto.setDestinatarios(mails.getOrDefault(Boolean.TRUE, empty));
            mails.clear();
        }

        if (nonNull(requestDto.getDestinatariosCc())) {
            mails = requestDto.getDestinatariosCc().stream().collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
            mails.getOrDefault(Boolean.FALSE, empty).forEach(x -> log.warn("[NotificacionesCorreoServiceImpl][validaCorreoSMTP]El destinatarioCc \"{}\" no cumple con el formato de correo ", x));
            requestDto.setDestinatariosCc(mails.getOrDefault(Boolean.TRUE, empty));
            mails.clear();
        }
        if (nonNull(requestDto.getDestinatariosBcc())) {
            mails = requestDto.getDestinatariosBcc().stream().collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
            mails.getOrDefault(Boolean.FALSE, empty).forEach(x -> log.warn("[NotificacionesCorreoServiceImpl][validaCorreoSMTP] El getDestinatariosBcc \"{}\" no cumple con el formato de correo ", x));
            requestDto.setDestinatariosBcc(mails.getOrDefault(Boolean.TRUE, empty));
            mails.clear();
        }
        validarCantidadDestinatarios(requestDto);

    }

    /**
     * Cantidad límite de destinatarios en los campos To:, Cc: y Bcc:. Si se envía
     * un correo a más destinatarios que los aquí especificados en cualquiera de los
     * tres campos, se enviará una advertencia a la autoridad responsable. Lo mismo
     * si la suma de las tres cantidades excede dos veces este límite.
     *
     * @param requestDto the request dto
     */
    public void validarCantidadDestinatarios(CorreoSMTPRequestDto requestDto) {
        int cantDestinatariosDirectos = 0;
        int cantDestinatariosCC = 0;
        int cantDestinatariosBCC = 0;
        String destinatarios = "";
        String destinatariosCc = "";
        String destinatariosBcc = "";

        if (requestDto.getDestinatarios() != null) {
            //cantDestinatariosDirectos = requestDto.getDestinatarios().size();
            cantDestinatariosDirectos = 1;
            destinatarios = requestDto.getDestinatarios().toString();
        }
        if (requestDto.getDestinatariosCc() != null) {
            cantDestinatariosCC = requestDto.getDestinatariosCc().size();
            destinatariosCc = requestDto.getDestinatariosCc().toString();
        }
        if (requestDto.getDestinatariosBcc() != null) {
            cantDestinatariosBCC = requestDto.getDestinatariosBcc().size();
            destinatariosBcc = requestDto.getDestinatariosBcc().toString();
        }

        Integer totalDestinatarios = cantDestinatariosDirectos + cantDestinatariosCC + cantDestinatariosBCC;

        if (totalDestinatarios <= 0) {
            log.info("No existen destinatarios, destinatariosCC o destinatariosBCC validos");
            throw new RuntimeException(Constantes.COD_VALIDACION_PARAMETROS_NO_VALIDOS);
        }

        if (cantDestinatariosDirectos > this.cantidadLimiteDestinatarios
                || cantDestinatariosCC > this.cantidadLimiteDestinatarios
                || cantDestinatariosBCC > this.cantidadLimiteDestinatarios
                || totalDestinatarios > (this.cantidadLimiteDestinatarios * 2)) {

            StringBuilder msgWarn = new StringBuilder(
                    "[Warning Mail] - [ Cantidad límite de destinatarios en los campos To:, Cc: y Bcc excede el limite recomendado. [");
            msgWarn.append(" \n nombreRemitente: ")
                    .append(requestDto.getNombreRemitente())
                    .append(" \n mailRemitente: ")
                    .append(requestDto.getCorreoRemitente())
                    .append(" \n Asunto: ")
                    .append(requestDto.getAsunto());
            msgWarn.append(" \n Destinatarios: ").append(destinatarios);
            msgWarn.append(" \n Destinatarios CC: ").append(destinatariosCc);
            msgWarn.append(" \n Destinatarios BCC: ").append(destinatariosBcc);
            msgWarn.append(" \n cuerpoTexto: ").append(requestDto.getCuerpoTexto()).append("]");

            log.warn("[NotificacionesCorreoServiceImpl][validarCantidadDestinatarios][BCI_FINOK] metodo ejecutado con advertencia: " + msgWarn);
        }
    }

}
