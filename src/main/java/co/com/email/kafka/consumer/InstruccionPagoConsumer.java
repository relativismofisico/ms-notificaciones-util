package co.com.email.kafka.consumer;

import co.com.email.domain.event.InstruccionPagoEvent;
import co.com.email.domain.event.NotificacionEmailEvent;
import co.com.email.service.CanalProcessorFactory;
import co.com.email.service.EmailProcessorService;
import co.com.email.util.InstruccionPagoEmailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstruccionPagoConsumer {

    private final CanalProcessorFactory factory;

    @KafkaListener(
            topics = "instrucciones-generadas",
            groupId = "notificaciones-group"
    )
    public void consumir(InstruccionPagoEvent event) {

        log.info("📥 Evento recibido: {}", event);

        if (event == null || event.getInstruccionId() == null) {
            log.warn("Evento ignorado - no es InstruccionPagoEvent válido");
            return;
        }

        factory.get(event.getCanalEnvio().name())
                .procesar(event);
    }
}
