package co.com.email.scheduler;

import co.com.email.domain.entities.OutboxEventEntity;
import co.com.email.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpEmailScheduler {

    private static final String MS_PROPIETARIO = "ms-notificaciones-util";

    private final OutboxEventRepository outboxEventRepository;
    private final OtpOutboxProcessor outboxProcessor;

    @Scheduled(fixedDelayString = "5000")
    public void enviarEmailsPendientes() {
        List<OutboxEventEntity> pendientes =
                outboxEventRepository.findTop50ByEnviadoFalseOrderByFechaCreacionAsc();

        for (OutboxEventEntity event : pendientes) {
            if (!MS_PROPIETARIO.equals(event.getMsPropietario())) {
                log.info("Evento {} ignorado, pertenece a {}", event.getId(), event.getMsPropietario());
                continue;
            }
            outboxProcessor.procesar(event);
        }
    }
}