package co.com.email.service;

import co.com.email.dto.CorreoSMTPRequestDto;
import co.com.email.util.TextosUtil;
import co.com.email.validation.CorreoSMTPValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacionesCorreoServiceImpl implements NotificacionesCorreoService {

    private final NotificaionCorreoSMTPClient notificacionCorreoSMTPClient;
    private final CorreoSMTPValidator correoSMTPValidator;

    @Override
    public void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto) {
        log.info("[NotificacionesCorreoServiceImpl][enviarCorreoSMTP] Inicio enviarCorreoSMTP: {}",
                correoSMTPRequestDto);

        correoSMTPValidator.validar(correoSMTPRequestDto);

        if (correoSMTPRequestDto.getNombreRemitente() == null) {
            correoSMTPRequestDto.setNombreRemitente("");
        } else {
            correoSMTPRequestDto.setNombreRemitente(correoSMTPRequestDto.getNombreRemitente().trim());
        }

        correoSMTPRequestDto.setFirma(TextosUtil.prepararFirmaCorreo(correoSMTPRequestDto.getFirma()));
        notificacionCorreoSMTPClient.enviarCorreoSMTP(correoSMTPRequestDto);
    }
}
