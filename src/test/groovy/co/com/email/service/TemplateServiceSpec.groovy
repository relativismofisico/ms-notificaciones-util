package co.com.email.service

import co.com.email.domain.entities.MessageTemplate
import co.com.email.repositories.MessageTemplateRepository
import co.com.email.util.template.TemplateRenderer
import spock.lang.Specification

class TemplateServiceSpec extends Specification {

    MessageTemplateRepository repository = Mock()
    TemplateRenderer renderer = Mock()
    TemplateService service = new TemplateService(repository, renderer)

    def buildTemplate(String name, String content, String subject) {
        MessageTemplate tpl = new MessageTemplate()
        tpl.templateName = name
        tpl.content = content
        tpl.emailSubject = subject
        tpl.active = true
        return tpl
    }

    def "build retorna HTML renderizado cuando plantilla existe"() {
        given:
        def template = buildTemplate("bienvenida", "<h1>Hola {{nombre}}</h1>", "Bienvenido")
        repository.findByTemplateNameAndActiveTrue("bienvenida") >> Optional.of(template)
        renderer.render("<h1>Hola {{nombre}}</h1>", [nombre: "Juan"]) >> "<h1>Hola Juan</h1>"

        when:
        def result = service.build("bienvenida", [nombre: "Juan"])

        then:
        result == "<h1>Hola Juan</h1>"
    }

    def "build lanza RuntimeException cuando plantilla no existe"() {
        given:
        repository.findByTemplateNameAndActiveTrue("no-existe") >> Optional.empty()

        when:
        service.build("no-existe", [:])

        then:
        def ex = thrown(RuntimeException)
        ex.message == "Template no encontrado"
    }

    def "getSubject retorna asunto cuando plantilla existe"() {
        given:
        def template = buildTemplate("otp", "<p>Tu OTP: {{otp}}</p>", "Tu código OTP")
        repository.findByTemplateNameAndActiveTrue("otp") >> Optional.of(template)

        when:
        def result = service.getSubject("otp")

        then:
        result == "Tu código OTP"
    }

    def "getSubject retorna 'Sin asunto' cuando plantilla no existe"() {
        given:
        repository.findByTemplateNameAndActiveTrue("no-existe") >> Optional.empty()

        when:
        def result = service.getSubject("no-existe")

        then:
        result == "Sin asunto"
    }

    def "build invoca render con el contenido de la plantilla"() {
        given:
        def template = buildTemplate("test", "contenido", "Asunto")
        repository.findByTemplateNameAndActiveTrue("test") >> Optional.of(template)
        renderer.render("contenido", [key: "val"]) >> "resultado"

        when:
        service.build("test", [key: "val"])

        then:
        1 * renderer.render("contenido", [key: "val"])
    }
}