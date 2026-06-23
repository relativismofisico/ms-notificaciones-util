package co.com.email.scheduler

import co.com.email.domain.entities.OutboxEventEntity
import co.com.email.domain.event.OtpCreatedEvent
import co.com.email.repositories.OutboxEventRepository
import co.com.email.service.OtpEmailService
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class OtpOutboxProcessorSpec extends Specification {

    OtpEmailService otpEmailService = Mock()
    OutboxEventRepository outboxEventRepository = Mock()
    ObjectMapper objectMapper = new ObjectMapper()
    OtpOutboxProcessor processor = new OtpOutboxProcessor(otpEmailService, outboxEventRepository, objectMapper)

    def buildEvent(String payload) {
        OutboxEventEntity entity = new OutboxEventEntity()
        entity.id = "evt-001"
        entity.payload = payload
        entity.enviado = false
        entity.intentos = 0
        return entity
    }

    def "procesar deserializa el payload, envía el email y marca como enviado"() {
        given:
        def otpEvent = OtpCreatedEvent.builder().rutOperador("jperez").otp("123456").build()
        String json = objectMapper.writeValueAsString(otpEvent)
        def entity = buildEvent(json)

        when:
        processor.procesar(entity)

        then:
        1 * otpEmailService.sendOtpEmail({ OtpCreatedEvent e ->
            e.rutOperador == "jperez" && e.otp == "123456"
        })
        1 * outboxEventRepository.save({ OutboxEventEntity e ->
            e.enviado == true && e.fechaEnvio != null
        })
    }

    def "procesar en caso de error incrementa intentos y guarda"() {
        given:
        def entity = buildEvent("{\"rutOperador\":\"jperez\",\"otp\":\"123456\"}")
        entity.intentos = 2
        otpEmailService.sendOtpEmail(_) >> { throw new RuntimeException("Error de envío") }

        when:
        processor.procesar(entity)

        then:
        1 * outboxEventRepository.save({ OutboxEventEntity e ->
            e.intentos == 3
        })
    }

    def "procesar con payload JSON inválido incrementa intentos y no lanza excepción"() {
        given:
        def entity = buildEvent("payload-invalido-{}")
        entity.intentos = 0

        when:
        processor.procesar(entity)

        then:
        1 * outboxEventRepository.save({ OutboxEventEntity e ->
            e.intentos == 1
        })
        0 * otpEmailService.sendOtpEmail(_)
    }
}