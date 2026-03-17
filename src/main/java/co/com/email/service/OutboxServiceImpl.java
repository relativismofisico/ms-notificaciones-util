package co.com.email.service;

import co.com.email.domain.entities.OutboxEventEntity;
import co.com.email.repositories.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void guardarEvento(String aggregateId, String tipoEvento, Object payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);

            OutboxEventEntity event = new OutboxEventEntity();
            event.setAggregateId(aggregateId);
            event.setTipoEvento(tipoEvento);
            event.setEventType(payload.getClass().getSimpleName());
            event.setPayload(payloadJson);
            event.setEnviado(false);
            event.setMsPropietario("ms-notificaciones-util");
            event.setFechaCreacion(LocalDateTime.now());
            event.setIntentos(0);

            outboxEventRepository.save(event);
            System.out.println("Evento guardado en OUTBOX -> TOPIC: " + tipoEvento);


        } catch (Exception e) {
            throw new RuntimeException("Error serializando payload para Outbox", e);
        }
    }
}
