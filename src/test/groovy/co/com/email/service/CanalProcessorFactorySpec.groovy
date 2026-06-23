package co.com.email.service

import co.com.email.domain.event.InstruccionPagoEvent
import spock.lang.Specification

class CanalProcessorFactorySpec extends Specification {

    def "get retorna processor que soporta el canal dado"() {
        given:
        CanalProcessor emailProcessor = Mock()
        emailProcessor.soporta("EMAIL") >> true
        CanalProcessor ftpProcessor = Mock()
        ftpProcessor.soporta("EMAIL") >> false

        CanalProcessorFactory factory = new CanalProcessorFactory([ftpProcessor, emailProcessor])

        when:
        def result = factory.get("EMAIL")

        then:
        result == emailProcessor
    }

    def "get retorna primer processor que soporta el canal"() {
        given:
        CanalProcessor p1 = Mock()
        p1.soporta("FTP") >> false
        CanalProcessor p2 = Mock()
        p2.soporta("FTP") >> true

        CanalProcessorFactory factory = new CanalProcessorFactory([p1, p2])

        when:
        def result = factory.get("FTP")

        then:
        result == p2
    }

    def "get lanza RuntimeException cuando ningún processor soporta el canal"() {
        given:
        CanalProcessor p1 = Mock()
        p1.soporta("DESCONOCIDO") >> false

        CanalProcessorFactory factory = new CanalProcessorFactory([p1])

        when:
        factory.get("DESCONOCIDO")

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("DESCONOCIDO")
    }

    def "get con lista vacía lanza RuntimeException"() {
        given:
        CanalProcessorFactory factory = new CanalProcessorFactory([])

        when:
        factory.get("EMAIL")

        then:
        thrown(RuntimeException)
    }
}