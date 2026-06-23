package co.com.email.service;

import co.com.email.domain.entities.EmailLog;
import co.com.email.domain.event.DestinatarioEmail;
import co.com.email.domain.event.NotificacionEmailEvent;
import co.com.email.repositories.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessorServiceImpl implements EmailProcessorService {

    private final ActorEmailResolverFactory resolverFactory;
    private final EmailLogRepository emailLogRepository;
    private final EmailSenderService emailSenderService;
    private final TemplateService templateService;

    @Override
    @SuppressWarnings("PMD.UnusedLocalVariable")
    public void procesar(NotificacionEmailEvent event) {
        log.info("Buscando plantilla con nombre: {}", event.getAsunto());

        for (DestinatarioEmail dest : event.getDestinatarios()) {

            ActorEmailResolver resolver = resolverFactory.get(dest.getTipoActor());
            String email = resolver.getEmail(dest.getRutActor());
            String nombre = resolver.getNombre(dest.getRutActor());

            log.info("Procesando destinatario RUT: {} - email obtenido: {}", dest.getRutActor(), email);

            if (email == null) {
                log.warn("Destinatario sin email. RUT: {}", dest.getRutActor());
                continue;
            }

            try {
                String html = templateService.build(event.getAsunto(), event.getData());

                String rutaArchivo = (String) event.getData().get("rutaArchivo");
                String nombreArchivo = (String) event.getData().get("archivo");

                if (rutaArchivo != null) {
                    emailSenderService.sendHtmlEmailWithAttachment(email, event.getAsunto(), html, rutaArchivo, nombreArchivo);
                } else {
                    emailSenderService.sendHtmlEmail(email, event.getAsunto(), html);
                }

                guardarLog(email, event.getAsunto(), "ENVIADO", null);
                log.info("Correo enviado a {}", email);

            } catch (Exception e) {
                log.error("Error enviando correo a {}", email, e);
                guardarLog(email, event.getAsunto(), "ERROR", e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    private void guardarLog(String email, String asunto, String estado, String error) {
        EmailLog logEntry = EmailLog.builder()
                .destinatario(email)
                .asunto(asunto)
                .estado(estado)
                .error(error)
                .fecha(LocalDateTime.now())
                .build();
        emailLogRepository.save(logEntry);
    }
}
