package co.com.email.service;

import co.com.email.domain.entities.EmailLog;
import co.com.email.domain.entities.MessageTemplate;
import co.com.email.domain.event.DestinatarioEmail;
import co.com.email.domain.event.NotificacionEmailEvent;
import co.com.email.repositories.EmailLogRepository;
import co.com.email.repositories.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessorServiceImpl implements EmailProcessorService {


    private final CompanyEmailService companyEmailService;
    private final MessageTemplateRepository templateRepository;

    private final EmailLogRepository emailLogRepository;
    private final UserEmailService userEmailService;
    private final EmailSenderService emailSenderService;

    @Override
    public void procesar(NotificacionEmailEvent event) {

        log.info("Buscando plantilla con nombre: {}", event.getAsunto());
        // 1. Resolvemos cual plantilla usar
         MessageTemplate template = templateRepository
                .findByTemplateName(event.getAsunto())
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrado"));


        // 2. Por cada destinatario
        for (DestinatarioEmail dest : event.getDestinatarios()) {

            String email;
            String nombre;

            if ("EMPRESA".equalsIgnoreCase(dest.getTipoActor())) {

                email = companyEmailService.getEmailByNit(dest.getRutActor());
                nombre = companyEmailService.getNombreByNit(dest.getRutActor());

            } else {

                email = userEmailService.getEmailByUsername(dest.getRutActor());
                nombre = userEmailService.getFullNameByUsername(dest.getRutActor());
            }

            log.info("Procesando destinatario RUT: {} - email obtenido: {}", dest.getRutActor(), email);
            // 🔥 VALIDACIÓN CLAVE
            if (email == null) {
                log.warn("Destinatario sin email. RUT: {}", dest.getRutActor());
                continue;
            }

            try {
                String html = template.getContent()
                        .replace("{{rutPagador}}", nombre)
                        .replace("{{tipoOperacion}}", event.getTipoNotificacion());


                emailSenderService.sendHtmlEmail(email, event.getAsunto(), html);
                guardarLog(dest.getEmail(), event.getAsunto(), "ENVIADO", null);
                log.info("✅ Correo enviado a {}", email);

            } catch (Exception e) {
                log.error("Error enviando correo a {}", dest.getEmail(), e);
                // no rompas todo el proceso por un solo fallo
                guardarLog(dest.getEmail(), event.getAsunto(), "ERROR", e.getMessage());            }
        }
    }
    private void guardarLog(String email, String asunto, String estado, String error) {

        EmailLog log = EmailLog.builder()
                .destinatario(email)
                .asunto(asunto)
                .estado(estado)
                .error(error)
                .fecha(LocalDateTime.now())
                .build();

        emailLogRepository.save(log);
    }


}