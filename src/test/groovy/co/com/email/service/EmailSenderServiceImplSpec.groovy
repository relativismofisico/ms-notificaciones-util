package co.com.email.service

import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification

import java.io.File

class EmailSenderServiceImplSpec extends Specification {

    JavaMailSender mailSender = Mock()
    EmailSenderServiceImpl service = new EmailSenderServiceImpl(mailSender)
    MimeMessage mimeMessage = Mock()

    def "sendHtmlEmail crea mensaje y lo envía"() {
        given:
        mailSender.createMimeMessage() >> mimeMessage
        mimeMessage.getDataHandler() >> null

        when:
        service.sendHtmlEmail("dest@test.com", "Asunto", "<p>Hola</p>")

        then:
        1 * mailSender.send(mimeMessage)
    }

    def "sendHtmlEmail lanza RuntimeException cuando mailSender falla"() {
        given:
        mailSender.createMimeMessage() >> mimeMessage
        mimeMessage.getDataHandler() >> null
        mailSender.send(_ as MimeMessage) >> { throw new RuntimeException("SMTP error") }

        when:
        service.sendHtmlEmail("dest@test.com", "Asunto", "<p>Hola</p>")

        then:
        RuntimeException ex = thrown()
        ex.message.contains("Error enviando correo")
    }

    def "sendHtmlEmailWithAttachment crea mensaje con adjunto cuando archivo existe"() {
        given:
        File tempFile = File.createTempFile("test-attachment", ".txt")
        tempFile.text = "contenido de prueba"
        tempFile.deleteOnExit()
        mailSender.createMimeMessage() >> mimeMessage
        mimeMessage.getDataHandler() >> null

        when:
        service.sendHtmlEmailWithAttachment(
                "dest@test.com", "Asunto", "<p>Con adjunto</p>",
                tempFile.absolutePath, "adjunto.txt")

        then:
        1 * mailSender.send(mimeMessage)
    }

    def "sendHtmlEmailWithAttachment envía sin adjunto cuando archivo no existe"() {
        given:
        mailSender.createMimeMessage() >> mimeMessage
        mimeMessage.getDataHandler() >> null

        when:
        service.sendHtmlEmailWithAttachment(
                "dest@test.com", "Asunto", "<p>Sin adjunto</p>",
                "/ruta/que/no/existe/archivo.pdf", "archivo.pdf")

        then:
        1 * mailSender.send(mimeMessage)
    }

    def "sendHtmlEmailWithAttachment lanza RuntimeException cuando mailSender falla"() {
        given:
        mailSender.createMimeMessage() >> mimeMessage
        mimeMessage.getDataHandler() >> null
        mailSender.send(_ as MimeMessage) >> { throw new RuntimeException("SMTP error") }

        when:
        service.sendHtmlEmailWithAttachment(
                "dest@test.com", "Asunto", "<p>Error</p>",
                "/no/existe.pdf", "archivo.pdf")

        then:
        RuntimeException ex = thrown()
        ex.message.contains("Error enviando email con adjunto")
    }
}