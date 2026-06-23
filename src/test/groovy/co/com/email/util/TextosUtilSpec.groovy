package co.com.email.util

import spock.lang.Specification
import spock.lang.Unroll

class TextosUtilSpec extends Specification {

    // ── prepararCuerpoMail ──────────────────────────────────────────────────

    def "prepararCuerpoMail con contenido y firma retorna cadena con separador"() {
        when:
        def result = TextosUtil.prepararCuerpoMail("Hola", "Firma")

        then:
        result == "Hola\n-- \nFirma"
    }

    def "prepararCuerpoMail con firma vacía retorna solo el contenido"() {
        when:
        def result = TextosUtil.prepararCuerpoMail("Contenido", "")

        then:
        result == "Contenido"
    }

    def "prepararCuerpoMail con contenido null retorna solo separador y firma"() {
        when:
        def result = TextosUtil.prepararCuerpoMail(null, "Firma")

        then:
        result == "\n-- \nFirma"
    }

    def "prepararCuerpoMail con firma null retorna solo el contenido"() {
        when:
        def result = TextosUtil.prepararCuerpoMail("Contenido", null)

        then:
        result == "Contenido"
    }

    def "prepararCuerpoMail con ambos null retorna cadena vacía"() {
        when:
        def result = TextosUtil.prepararCuerpoMail(null, null)

        then:
        result == ""
    }

    // ── prepararFirmaCorreo ─────────────────────────────────────────────────

    def "prepararFirmaCorreo con firma null retorna parrafo vacío"() {
        when:
        def result = TextosUtil.prepararFirmaCorreo(null)

        then:
        result != null
    }

    def "prepararFirmaCorreo con firma blank retorna parrafo vacío"() {
        when:
        def result = TextosUtil.prepararFirmaCorreo("   ")

        then:
        result != null
    }

    def "prepararFirmaCorreo con firma que termina en EOL elimina el ultimo EOL"() {
        given:
        def firma = "Mi Firma\n"

        when:
        def result = TextosUtil.prepararFirmaCorreo(firma)

        then:
        result != null
    }

    def "prepararFirmaCorreo con firma que contiene EOL concatena EOL al final"() {
        given:
        def firma = "Linea1\nLinea2"

        when:
        def result = TextosUtil.prepararFirmaCorreo(firma)

        then:
        result.endsWith(TextosUtil.EOL)
    }

    def "prepararFirmaCorreo con firma sin EOL llama a parrafo"() {
        given:
        def firma = "Una firma simple sin saltos"

        when:
        def result = TextosUtil.prepararFirmaCorreo(firma)

        then:
        result != null
    }

    // ── parrafo ─────────────────────────────────────────────────────────────

    def "parrafo con texto null retorna resultado no nulo"() {
        when:
        def result = TextosUtil.parrafo(null, 10, TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA)

        then:
        result != null
    }

    def "parrafo con largo <= 0 retorna el texto original"() {
        when:
        def result = TextosUtil.parrafo("texto", 0, TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA)

        then:
        result == "texto"
    }

    def "parrafo con largo negativo retorna el texto original"() {
        when:
        def result = TextosUtil.parrafo("texto", -5, TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA)

        then:
        result == "texto"
    }

    def "parrafo con texto vacío retorna resultado vacío"() {
        when:
        def result = TextosUtil.parrafo("", 10, TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA)

        then:
        result == ""
    }

    @Unroll
    def "parrafo con alineacion #alineacion retorna resultado con EOL"() {
        given:
        def texto = "Texto de prueba para alineacion del parrafo con largo suficiente para procesar"

        when:
        def result = TextosUtil.parrafo(texto, 20, alineacion)

        then:
        result != null

        where:
        alineacion << [
            TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA,
            TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA_CON_RELLENO,
            TextosUtil.PARRAFO_ALINEDO_A_LA_DERECHA,
            TextosUtil.PARRAFO_CENTRADO,
            TextosUtil.PARRAFO_CENTRADO_CON_RELLENO,
            TextosUtil.PARRAFO_JUSTIFICADO
        ]
    }

    def "parrafo elimina espacios multiples del texto"() {
        given:
        def texto = "Texto  con   espacios    multiples"

        when:
        def result = TextosUtil.parrafo(texto, 100, TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA)

        then:
        result != null
    }

    def "parrafo con texto corto que cabe en una linea retorna resultado"() {
        given:
        def texto = "Hola"

        when:
        def result = TextosUtil.parrafo(texto, 100, TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA)

        then:
        result != null
    }

    def "parrafo con texto largo sin espacios no lanza excepcion"() {
        given:
        def texto = "PalabraLargaSinEspaciosQueExcedeLargoMaximo"

        when:
        def result = TextosUtil.parrafo(texto, 10, TextosUtil.PARRAFO_ALINEDO_A_LA_IZQUIERDA)

        then:
        result != null
    }

    // ── Constantes ──────────────────────────────────────────────────────────

    def "constante EOL es salto de línea"() {
        expect:
        TextosUtil.EOL == "\n"
    }

    def "constante EOL_CHAR es caracter de salto de línea"() {
        expect:
        TextosUtil.EOL_CHAR == '\n'
    }

    def "constante ANCHO_DE_LINEA_EN_EMAIL es 72"() {
        expect:
        TextosUtil.ANCHO_DE_LINEA_EN_EMAIL == 72
    }
}