package co.com.email.scheduler;

import co.com.email.domain.entities.OutboxEventEntity;
import co.com.email.domain.event.OtpCreatedEvent;
import co.com.email.repositories.OutboxEventRepository;
import co.com.email.service.OtpEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpOutboxProcessor {

    private final OtpEmailService otpEmailService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void procesar(OutboxEventEntity event) {
        try {
            OtpCreatedEvent otpEvent = objectMapper.readValue(event.getPayload(), OtpCreatedEvent.class);
            otpEmailService.sendOtpEmail(otpEvent);
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
