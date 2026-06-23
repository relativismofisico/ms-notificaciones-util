package co.com.email.service

import co.com.email.domain.entities.ConfiguracionFtp
import co.com.email.domain.event.InstruccionPagoEvent
import co.com.email.repositories.FtpConfiguracionRepository
import spock.lang.Specification

class FtpCanalProcessorSpec extends Specification {

    FtpConfiguracionRepository ftpRepository = Mock()
    FtpService ftpService = Mock()
    FtpCanalProcessor processor = new FtpCanalProcessor(ftpRepository, ftpService)

    def "soporta retorna true para canal FTP"() {
        expect:
        processor.soporta("FTP")
    }

    def "soporta retorna true para canal ftp en minúscula"() {
        expect:
        processor.soporta("ftp")
    }

    def "soporta retorna false para canal EMAIL"() {
        expect:
        !processor.soporta("EMAIL")
    }

    def "procesar llama a ftpService con configuración correcta"() {
        given:
        def config = new ConfiguracionFtp()
        config.empresa = "FOND-001"
        config.host = "sftp.test.com"
        ftpRepository.findByEmpresa("FOND-001") >> Optional.of(config)

        def event = InstruccionPagoEvent.builder()
                .fondeadorId("FOND-001")
                .rutaArchivo("/tmp/archivo.xlsx")
                .nombreArchivo("archivo.xlsx")
                .build()

        when:
        processor.procesar(event)

        then:
        1 * ftpService.subirArchivo(config, "/tmp/archivo.xlsx", "archivo.xlsx")
    }

    def "procesar lanza RuntimeException cuando no existe configuración FTP"() {
        given:
        ftpRepository.findByEmpresa("FOND-NO-EXISTE") >> Optional.empty()

        def event = InstruccionPagoEvent.builder()
                .fondeadorId("FOND-NO-EXISTE")
                .rutaArchivo("/tmp/archivo.xlsx")
                .nombreArchivo("archivo.xlsx")
                .build()

        when:
        processor.procesar(event)

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("FOND-NO-EXISTE")
    }

    def "procesar relanza excepción cuando ftpService lanza error"() {
        given:
        def config = new ConfiguracionFtp()
        ftpRepository.findByEmpresa("FOND-001") >> Optional.of(config)
        ftpService.subirArchivo(_, _, _) >> { throw new RuntimeException("Error SFTP") }

        def event = InstruccionPagoEvent.builder()
                .fondeadorId("FOND-001")
                .rutaArchivo("/tmp/a.xlsx")
                .nombreArchivo("a.xlsx")
                .build()

        when:
        processor.procesar(event)

        then:
        thrown(RuntimeException)
    }
}