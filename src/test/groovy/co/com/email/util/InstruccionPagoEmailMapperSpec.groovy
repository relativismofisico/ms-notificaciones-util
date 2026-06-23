package co.com.email.util

import co.com.email.domain.event.InstruccionPagoEvent
import co.com.email.enums.CanalEnvio
import spock.lang.Specification

import java.math.BigDecimal
import java.time.Instant

class InstruccionPagoEmailMapperSpec extends Specification {

    InstruccionPagoEmailMapper mapper = new InstruccionPagoEmailMapper()

    def "toEmailEvent mapea correctamente un InstruccionPagoEvent"() {
        given:
        def event = InstruccionPagoEvent.builder()
                .instruccionId("INS-001")
                .fondeadorId("FOND-001")
                .pagadorId("PAG-001")
                .pagadorNombre("Pagador SA")
                .cantidadFacturas(5)
                .totalPagar(new BigDecimal("1500000.00"))
                .nombreArchivo("instruccion.xlsx")
                .rutaArchivo("/tmp/instruccion.xlsx")
                .fechaGeneracion(Instant.now())
                .canalEnvio(CanalEnvio.EMAIL)
                .build()

        when:
        def result = mapper.toEmailEvent(event)

        then:
        result != null
        result.asunto == "INSTRUCCIONES_PAGO_GENERADAS"
        result.destinatarios.size() == 1
        result.destinatarios[0].tipoActor == "EMPRESA"
        result.destinatarios[0].rutActor == "FOND-001"
        result.data["pagador"] == "Pagador SA"
        result.data["cantidadFacturas"] == 5
        result.data["totalPagar"] == new BigDecimal("1500000.00")
        result.data["archivo"] == "instruccion.xlsx"
        result.data["rutaArchivo"] == "/tmp/instruccion.xlsx"
    }

    def "toEmailEvent con fondeadorId null mapea rutActor como null"() {
        given:
        def event = InstruccionPagoEvent.builder()
                .fondeadorId(null)
                .pagadorNombre("Pagador SA")
                .cantidadFacturas(1)
                .totalPagar(BigDecimal.ZERO)
                .nombreArchivo("archivo.xlsx")
                .rutaArchivo("/tmp/archivo.xlsx")
                .build()

        when:
        def result = mapper.toEmailEvent(event)

        then:
        result.destinatarios[0].rutActor == null
    }

    def "toEmailEvent siempre genera exactamente un destinatario de tipo EMPRESA"() {
        given:
        def event = InstruccionPagoEvent.builder()
                .fondeadorId("FOND-XYZ")
                .pagadorNombre("P")
                .cantidadFacturas(1)
                .totalPagar(BigDecimal.ONE)
                .nombreArchivo("f.xlsx")
                .rutaArchivo("/f.xlsx")
                .build()

        when:
        def result = mapper.toEmailEvent(event)

        then:
        result.destinatarios.size() == 1
        result.destinatarios[0].tipoActor == "EMPRESA"
    }
}