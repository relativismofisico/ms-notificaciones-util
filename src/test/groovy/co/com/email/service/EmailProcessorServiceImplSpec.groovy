package co.com.email.service

import co.com.email.domain.entities.EmailLog
import co.com.email.domain.event.DestinatarioEmail
import co.com.email.domain.event.NotificacionEmailEvent
import co.com.email.repositories.EmailLogRepository
import spock.lang.Specification

class EmailProcessorServiceImplSpec extends Specification {

    ActorEmailResolverFactory resolverFactory = Mock()
    EmailLogRepository emailLogRepository = Mock()
    EmailSenderService emailSenderService = Mock()
    TemplateService templateService = Mock()
    EmailProcessorServiceImpl service = new EmailProcessorServiceImpl(
            resolverFactory, emailLogRepository, emailSenderService, templateService)

    ActorEmailResolver resolver = Mock()

    def buildEvent(List<DestinatarioEmail> destinatarios, Map<String, Object> data = [:]) {
        return NotificacionEmailEvent.builder()
                .asunto("INSTRUCCIONES_PAGO_GENERADAS")
                .destinatarios(destinatarios)
                .data(data)
                .build()
    }

    def "procesar envía email cuando destinatario tiene email válido"() {
        given:
        def dest = DestinatarioEmail.builder().tipoActor("EMPRESA").rutActor("FOND-001").build()
        def event = buildEvent([dest], ["key": "val"])
        resolverFactory.get("EMPRESA") >> resolver
        resolver.getEmail("FOND-001") >> "empresa@test.com"
        resolver.getNombre("FOND-001") >> "Empresa Test"
        templateService.build("INSTRUCCIONES_PAGO_GENERADAS", _) >> "<h1>Contenido</h1>"

        when:
        service.procesar(event)

        then:
        1 * emailSenderService.sendHtmlEmail("empresa@test.com", "INSTRUCCIONES_PAGO_GENERADAS", "<h1>Contenido</h1>")
        1 * emailLogRepository.save({ EmailLog log ->
            log.destinatario == "empresa@test.com" &&
            log.estado == "ENVIADO" &&
            log.error == null
        })
    }

    def "procesar salta destinatario cuando email es null"() {
        given:
        def dest = DestinatarioEmail.builder().tipoActor("EMPRESA").rutActor("FOND-SIN-EMAIL").build()
        def event = buildEvent([dest])
        resolverFactory.get("EMPRESA") >> resolver
        resolver.getEmail("FOND-SIN-EMAIL") >> null

        when:
        service.procesar(event)

        then:
        0 * emailSenderService.sendHtmlEmail(_, _, _)
        0 * emailLogRepository.save(_)
    }

    def "procesar envía con adjunto cuando rutaArchivo está en data"() {
        given:
        def dest = DestinatarioEmail.builder().tipoActor("EMPRESA").rutActor("FOND-001").build()
        def data = ["rutaArchivo": "/tmp/file.xlsx", "archivo": "file.xlsx"] as Map<String, Object>
        def event = buildEvent([dest], data)
        resolverFactory.get("EMPRESA") >> resolver
        resolver.getEmail("FOND-001") >> "empresa@test.com"
        resolver.getNombre("FOND-001") >> "Empresa"
        templateService.build(_, _) >> "<html/>"

        when:
        service.procesar(event)

        then:
        1 * emailSenderService.sendHtmlEmailWithAttachment(
                "empresa@test.com", "INSTRUCCIONES_PAGO_GENERADAS", "<html/>",
                "/tmp/file.xlsx", "file.xlsx")
        0 * emailSenderService.sendHtmlEmail(_, _, _)
    }

    def "procesar guarda log de error cuando emailSenderService lanza excepción"() {
        given:
        def dest = DestinatarioEmail.builder().tipoActor("EMPRESA").rutActor("FOND-001").build()
        def event = buildEvent([dest])
        resolverFactory.get("EMPRESA") >> resolver
        resolver.getEmail("FOND-001") >> "empresa@test.com"
        resolver.getNombre("FOND-001") >> "Empresa"
        templateService.build(_, _) >> "<html/>"
        emailSenderService.sendHtmlEmail(_, _, _) >> { throw new RuntimeException("Error SMTP") }

        when:
        service.procesar(event)

        then:
        1 * emailLogRepository.save({ EmailLog log ->
            log.estado == "ERROR" && log.error != null && log.error.contains("Error SMTP")
        })
    }

    def "procesar maneja múltiples destinatarios independientemente"() {
        given:
        def dest1 = DestinatarioEmail.builder().tipoActor("EMPRESA").rutActor("FOND-001").build()
        def dest2 = DestinatarioEmail.builder().tipoActor("EMPRESA").rutActor("FOND-002").build()
        def event = buildEvent([dest1, dest2])
        resolverFactory.get("EMPRESA") >> resolver
        resolver.getEmail("FOND-001") >> "f1@test.com"
        resolver.getNombre("FOND-001") >> "F1"
        resolver.getEmail("FOND-002") >> "f2@test.com"
        resolver.getNombre("FOND-002") >> "F2"
        templateService.build(_, _) >> "<html/>"

        when:
        service.procesar(event)

        then:
        1 * emailSenderService.sendHtmlEmail("f1@test.com", _, _)
        1 * emailSenderService.sendHtmlEmail("f2@test.com", _, _)
        2 * emailLogRepository.save(_)
    }
}