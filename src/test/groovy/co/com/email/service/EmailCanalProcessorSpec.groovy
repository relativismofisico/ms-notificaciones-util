package co.com.email.service

import co.com.email.domain.event.InstruccionPagoEvent
import co.com.email.domain.event.NotificacionEmailEvent
import co.com.email.enums.CanalEnvio
import co.com.email.util.InstruccionPagoEmailMapper
import spock.lang.Specification

class EmailCanalProcessorSpec extends Specification {

    EmailProcessorService emailProcessorService = Mock()
    InstruccionPagoEmailMapper mapper = Mock()
    EmailCanalProcessor processor = new EmailCanalProcessor(emailProcessorService, mapper)

    def "soporta retorna true para canal EMAIL"() {
        expect:
        processor.soporta("EMAIL")
    }

    def "soporta retorna true para canal email en minúscula"() {
        expect:
        processor.soporta("email")
    }

    def "soporta retorna true para canal Email mixto"() {
        expect:
        processor.soporta("Email")
    }

    def "soporta retorna false para canal FTP"() {
        expect:
        !processor.soporta("FTP")
    }

    def "soporta retorna false para canal desconocido"() {
        expect:
        !processor.soporta("SMS")
    }

    def "procesar mapea el evento y delega en emailProcessorService"() {
        given:
        def event = InstruccionPagoEvent.builder()
                .fondeadorId("FOND-001")
                .canalEnvio(CanalEnvio.EMAIL)
                .build()
        def emailEvent = NotificacionEmailEvent.builder().asunto("test").build()

        when:
        processor.procesar(event)

        then:
        1 * mapper.toEmailEvent(event) >> emailEvent
        1 * emailProcessorService.procesar(emailEvent)
    }
}