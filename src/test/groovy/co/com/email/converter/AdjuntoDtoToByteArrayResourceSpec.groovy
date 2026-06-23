package co.com.email.converter

import co.com.email.dto.AdjuntoDto
import spock.lang.Specification

import java.util.Base64

class AdjuntoDtoToByteArrayResourceSpec extends Specification {

    AdjuntoDtoToByteArrayResource converter = new AdjuntoDtoToByteArrayResource()

    def "convert con base64 válido retorna ByteArrayResource con bytes correctos"() {
        given:
        byte[] content = "contenido de prueba".bytes
        String base64 = Base64.encoder.encodeToString(content)
        AdjuntoDto dto = AdjuntoDto.builder()
                .nombreArchivo("archivo.txt")
                .extension("txt")
                .archivoBase64(base64)
                .build()

        when:
        def result = converter.convert(dto)

        then:
        result != null
        result.byteArray == content
    }

    def "convert con base64 de archivo vacío retorna ByteArrayResource vacío"() {
        given:
        String base64 = Base64.encoder.encodeToString(new byte[0])
        AdjuntoDto dto = AdjuntoDto.builder()
                .archivoBase64(base64)
                .build()

        when:
        def result = converter.convert(dto)

        then:
        result != null
        result.byteArray.length == 0
    }

    def "convert con base64 de PDF simulado retorna recurso con bytes"() {
        given:
        byte[] pdfBytes = [0x25, 0x50, 0x44, 0x46] as byte[] // %PDF
        String base64 = Base64.encoder.encodeToString(pdfBytes)
        AdjuntoDto dto = AdjuntoDto.builder()
                .nombreArchivo("documento.pdf")
                .extension("pdf")
                .archivoBase64(base64)
                .build()

        when:
        def result = converter.convert(dto)

        then:
        result != null
        result.byteArray == pdfBytes
    }
}