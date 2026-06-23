package co.com.email.util.template

import spock.lang.Specification

class TemplateRendererSpec extends Specification {

    TemplateRenderer renderer = new TemplateRenderer()

    def "render sustituye variable simple"() {
        when:
        def result = renderer.render("Hola {{nombre}}", [nombre: "Juan"])

        then:
        result == "Hola Juan"
    }

    def "render sustituye múltiples variables"() {
        when:
        def result = renderer.render("{{saludo}} {{nombre}}, tu OTP es {{otp}}", [saludo: "Hola", nombre: "Ana", otp: "123456"])

        then:
        result == "Hola Ana, tu OTP es 123456"
    }

    def "render con template sin variables retorna el contenido sin cambios"() {
        when:
        def result = renderer.render("Texto sin variables", [:])

        then:
        result == "Texto sin variables"
    }

    def "render con data vacía mantiene placeholders no resueltos como vacíos"() {
        when:
        def result = renderer.render("Hola {{nombre}}", [:])

        then:
        result == "Hola "
    }

    def "render con template HTML retorna HTML con variables sustituidas"() {
        given:
        def template = "<h1>Bienvenido {{nombre}}</h1><p>{{mensaje}}</p>"

        when:
        def result = renderer.render(template, [nombre: "Carlos", mensaje: "Tu cuenta ha sido creada"])

        then:
        result == "<h1>Bienvenido Carlos</h1><p>Tu cuenta ha sido creada</p>"
    }

    def "render con template vacío retorna cadena vacía"() {
        when:
        def result = renderer.render("", [:])

        then:
        result == ""
    }
}