package co.com.email.scheduler;

import co.com.email.domain.entities.OutboxEventEntity;
import co.com.email.domain.event.OtpCreatedEvent;
import co.com.email.repositories.OutboxEventRepository;
import co.com.email.service.OtpEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpEmailScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final OtpEmailService otpEmailService;
    private final ObjectMapper objectMapper;

    private static final String MS_PROPIETARIO = "ms-notificaciones-util";


    @Scheduled(fixedDelayString = "5000")
    public void enviarEmailsPendientes() {
        // Traer solo eventos pendientes de este MS
        List<OutboxEventEntity> pendientes =
                outboxEventRepository.findTop50ByEnviadoFalseOrderByFechaCreacionAsc()
                        .stream()
                        .sorted(Comparator.comparing(OutboxEventEntity::getFechaCreacion))
                        .toList();

        for (var event : pendientes) {
            if (!MS_PROPIETARIO.equals(event.getMsPropietario())) {
                log.info("Evento {} ignorado, pertenece a {}", event.getId(), event.getMsPropietario());
                continue;
            }
            try {
                OtpCreatedEvent otpEvent = objectMapper.readValue(event.getPayload(), OtpCreatedEvent.class);

                otpEmailService.sendOtpEmail(otpEvent);

                // marcar como enviado
                event.setEnviado(true);
                event.setFechaEnvio(LocalDateTime.now());
                outboxEventRepository.save(event);

            } catch (Exception e) {
                log.error("Error procesando evento {}: {}", event.getId(), e.getMessage(), e);
                event.setIntentos(event.getIntentos() + 1);
                outboxEventRepository.save(event);
            }
        }
    }
}
