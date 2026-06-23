package co.com.email.scheduler

import co.com.email.domain.entities.OutboxEventEntity
import co.com.email.repositories.OutboxEventRepository
import spock.lang.Specification

class OtpEmailSchedulerSpec extends Specification {

    OutboxEventRepository outboxEventRepository = Mock()
    OtpOutboxProcessor outboxProcessor = Mock()
    OtpEmailScheduler scheduler = new OtpEmailScheduler(outboxEventRepository, outboxProcessor)

    def buildEntity(String msPropietario) {
        OutboxEventEntity entity = new OutboxEventEntity()
        entity.id = UUID.randomUUID().toString()
        entity.msPropietario = msPropietario
        entity.enviado = false
        return entity
    }

    def "enviarEmailsPendientes procesa eventos del propietario correcto"() {
        given:
        def evento = buildEntity("ms-notificaciones-util")
        outboxEventRepository.findTop50ByEnviadoFalseOrderByFechaCreacionAsc() >> [evento]

        when:
        scheduler.enviarEmailsPendientes()

        then:
        1 * outboxProcessor.procesar(evento)
    }

    def "enviarEmailsPendientes ignora eventos de otro microservicio"() {
        given:
        def eventoAjeno = buildEntity("ms-otro-servicio")
        outboxEventRepository.findTop50ByEnviadoFalseOrderByFechaCreacionAsc() >> [eventoAjeno]

        when:
        scheduler.enviarEmailsPendientes()

        then:
        0 * outboxProcessor.procesar(_)
    }

    def "enviarEmailsPendientes no hace nada cuando no hay pendientes"() {
        given:
        outboxEventRepository.findTop50ByEnviadoFalseOrderByFechaCreacionAsc() >> []

        when:
        scheduler.enviarEmailsPendientes()

        then:
        0 * outboxProcessor.procesar(_)
    }

    def "enviarEmailsPendientes procesa solo eventos propios en lista mixta"() {
        given:
        def propioA = buildEntity("ms-notificaciones-util")
        def ajeno = buildEntity("ms-otro")
        def propioB = buildEntity("ms-notificaciones-util")
        outboxEventRepository.findTop50ByEnviadoFalseOrderByFechaCreacionAsc() >> [propioA, ajeno, propioB]

        when:
        scheduler.enviarEmailsPendientes()

        then:
        1 * outboxProcessor.procesar(propioA)
        0 * outboxProcessor.procesar(ajeno)
        1 * outboxProcessor.procesar(propioB)
    }
}