package co.com.email.validation;

import co.com.email.dto.CorreoSMTPRequestDto;
import co.com.email.exception.EmailValidationException;
import co.com.email.util.ValidadorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CorreoSMTPValidator {

    private final int cantidadLimiteDestinatarios;

    public CorreoSMTPValidator(
            @Value("${cantidadLimiteDestinatarios}") int cantidadLimiteDestinatarios) {
        this.cantidadLimiteDestinatarios = cantidadLimiteDestinatarios;
    }

    public void validar(CorreoSMTPRequestDto requestDto) {
        List<String> empty = new ArrayList<>();

        if (!ValidadorUtil.validateEmail(requestDto.getCorreoRemitente())) {
            log.info("Parámetro correoRemitente not a well-formed email address");
            throw new EmailValidationException("El campo correoRemitente no tiene un formato de email válido");
        }

        if (requestDto.getFirma() == null || requestDto.getFirma().trim().isEmpty()) {
            log.warn("[CorreoSMTPValidator][validar] La firma es nula o vacía");
        }

        Map<Boolean, List<String>> mails;
        if (requestDto.getDestinatarios() != null) {
            mails = requestDto.getDestinatarios().stream()
                    .collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
            mails.getOrDefault(Boolean.FALSE, empty)
                    .forEach(x -> log.warn("[CorreoSMTPValidator] El destinatario \"{}\" no cumple con el formato de correo", x));
            requestDto.setDestinatarios(mails.getOrDefault(Boolean.TRUE, empty));
        }

        if (requestDto.getDestinatariosCc() != null) {
            mails = requestDto.getDestinatariosCc().stream()
                    .collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
            mails.getOrDefault(Boolean.FALSE, empty)
                    .forEach(x -> log.warn("[CorreoSMTPValidator] El destinatarioCc \"{}\" no cumple con el formato de correo", x));
            requestDto.setDestinatariosCc(mails.getOrDefault(Boolean.TRUE, empty));
        }

        if (requestDto.getDestinatariosBcc() != null) {
            mails = requestDto.getDestinatariosBcc().stream()
                    .collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
            mails.getOrDefault(Boolean.FALSE, empty)
                    .forEach(x -> log.warn("[CorreoSMTPValidator] El destinatarioBcc \"{}\" no cumple con el formato de correo", x));
            requestDto.setDestinatariosBcc(mails.getOrDefault(Boolean.TRUE, empty));
        }

        validarCantidadDestinatarios(requestDto);
    }

    private void validarCantidadDestinatarios(CorreoSMTPRequestDto requestDto) {
        int cantTo = requestDto.getDestinatarios() != null ? requestDto.getDestinatarios().size() : 0;
        int cantCc = requestDto.getDestinatariosCc() != null ? requestDto.getDestinatariosCc().size() : 0;
        int cantBcc = requestDto.getDestinatariosBcc() != null ? requestDto.getDestinatariosBcc().size() : 0;
        int total = cantTo + cantCc + cantBcc;

        if (total <= 0) {
            log.info("No existen destinatarios, destinatariosCC o destinatariosBCC validos");
            throw new EmailValidationException("No existen destinatarios válidos en los campos To, Cc o Bcc");
        }

        if (cantTo > cantidadLimiteDestinatarios
                || cantCc > cantidadLimiteDestinatarios
                || cantBcc > cantidadLimiteDestinatarios
                || total > (cantidadLimiteDestinatarios * 2)) {

            String destinatarios = requestDto.getDestinatarios() != null ? requestDto.getDestinatarios().toString() : "";
            String destinatariosCc = requestDto.getDestinatariosCc() != null ? requestDto.getDestinatariosCc().toString() : "";
            String destinatariosBcc = requestDto.getDestinatariosBcc() != null ? requestDto.getDestinatariosBcc().toString() : "";

            StringBuilder msgWarn = new StringBuilder(
                    "[Warning Mail] - [ Cantidad límite de destinatarios en los campos To:, Cc: y Bcc excede el limite recomendado. [");
            msgWarn.append(" \n nombreRemitente: ").append(requestDto.getNombreRemitente())
                    .append(" \n mailRemitente: ").append(requestDto.getCorreoRemitente())
                    .append(" \n Asunto: ").append(requestDto.getAsunto())
                    .append(" \n Destinatarios: ").append(destinatarios)
                    .append(" \n Destinatarios CC: ").append(destinatariosCc)
                    .append(" \n Destinatarios BCC: ").append(destinatariosBcc)
                    .append(" \n cuerpoTexto: ").append(requestDto.getCuerpoTexto()).append("]");

            log.warn("[CorreoSMTPValidator][validarCantidadDestinatarios] metodo ejecutado con advertencia: {}", msgWarn);
        }
    }
}