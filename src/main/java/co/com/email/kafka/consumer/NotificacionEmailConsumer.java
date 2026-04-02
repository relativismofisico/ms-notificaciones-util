package co.com.email.kafka.consumer;

import co.com.email.domain.event.NotificacionEmailEvent;
import co.com.email.domain.event.OtpCreatedEvent;
import co.com.email.service.EmailProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionEmailConsumer {

    private final EmailProcessorService emailProcessorService;

    @KafkaListener(
            topics = "${topics.notificacion-email}",
            groupId = "notificador-group")
    public void consumir(NotificacionEmailEvent event) {
        log.info("🔥 EVENTO RECIBIDO: {}", event);

        try {
            emailProcessorService.procesar(event);
        } catch (Exception e) {
            log.error("Error procesando evento de notificación", e);
            throw new RuntimeException(e);
        }
    }
}
