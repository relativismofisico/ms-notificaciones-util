package co.com.email.service

import co.com.email.repositories.OutboxEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class OutboxServiceImplSpec extends Specification {

    OutboxEventRepository outboxEventRepository = Mock()
    ObjectMapper objectMapper = new ObjectMapper()
    OutboxServiceImpl service = new OutboxServiceImpl(outboxEventRepository, objectMapper)

    def "guardarEvento persiste entidad con los campos correctos"() {
        given:
        def payload = [key: "valor", num: 42]

        when:
        service.guardarEvento("AGG-001", "TIPO_EVENTO", payload)

        then:
        1 * outboxEventRepository.save({ entity ->
            entity.aggregateId == "AGG-001" &&
            entity.tipoEvento == "TIPO_EVENTO" &&
            entity.enviado == false &&
            entity.msPropietario == "ms-notificaciones-util" &&
            entity.intentos == 0 &&
            entity.payload != null &&
            entity.fechaCreacion != null
        })
    }

    def "guardarEvento almacena el tipo de clase del payload en eventType"() {
        given:
        def payload = "string-payload"

        when:
        service.guardarEvento("AGG-002", "EVENTO", payload)

        then:
        1 * outboxEventRepository.save({ entity ->
            entity.eventType == "String"
        })
    }

    def "guardarEvento lanza RuntimeException cuando ObjectMapper falla"() {
        given:
        ObjectMapper failingMapper = Mock()
        failingMapper.writeValueAsString(_) >> { throw new RuntimeException("JSON error") }
        OutboxServiceImpl failingService = new OutboxServiceImpl(outboxEventRepository, failingMapper)

        when:
        failingService.guardarEvento("AGG", "TIPO", new Object())

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("Error serializando payload")
    }
}