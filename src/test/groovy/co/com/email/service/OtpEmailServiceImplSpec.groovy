package co.com.email.service

import co.com.email.domain.event.OtpCreatedEvent
import spock.lang.Specification

class OtpEmailServiceImplSpec extends Specification {

    UserEmailService userEmailService = Mock()
    EmailSenderService emailSenderService = Mock()
    TemplateService templateService = Mock()
    OtpEmailServiceImpl service = new OtpEmailServiceImpl(userEmailService, emailSenderService, templateService)

    def "sendOtpEmail obtiene email, renderiza plantilla y envía"() {
        given:
        def event = OtpCreatedEvent.builder()
                .rutOperador("jperez")
                .otp("123456")
                .build()
        userEmailService.getEmailByUsername("jperez") >> "jperez@test.com"
        userEmailService.getFullNameByUsername("jperez") >> "Juan Pérez"
        templateService.build("otp", _) >> "<p>Tu OTP: 123456</p>"
        templateService.getSubject("otp") >> "Tu código OTP"

        when:
        service.sendOtpEmail(event)

        then:
        1 * emailSenderService.sendHtmlEmail("jperez@test.com", "Tu código OTP", "<p>Tu OTP: 123456</p>")
    }

    def "sendOtpEmail pasa nombre y otp correctos al template"() {
        given:
        def event = OtpCreatedEvent.builder()
                .rutOperador("agarcia")
                .otp("654321")
                .build()
        userEmailService.getEmailByUsername("agarcia") >> "agarcia@test.com"
        userEmailService.getFullNameByUsername("agarcia") >> "Ana García"
        templateService.getSubject("otp") >> "OTP"

        when:
        service.sendOtpEmail(event)

        then:
        1 * templateService.build("otp", { Map<String, Object> data ->
            data.get("nombre") == "Ana García" && data.get("otp") == "654321"
        }) >> "<p>OTP</p>"
        1 * emailSenderService.sendHtmlEmail("agarcia@test.com", "OTP", "<p>OTP</p>")
    }

    def "sendOtpEmail invoca getEmailByUsername y getFullNameByUsername exactamente una vez"() {
        given:
        def event = OtpCreatedEvent.builder().rutOperador("usr").otp("999").build()
        templateService.build("otp", _) >> "<p>ok</p>"
        templateService.getSubject("otp") >> "OTP"

        when:
        service.sendOtpEmail(event)

        then:
        1 * userEmailService.getEmailByUsername("usr") >> "usr@test.com"
        1 * userEmailService.getFullNameByUsername("usr") >> "Usuario Test"
        1 * emailSenderService.sendHtmlEmail("usr@test.com", "OTP", "<p>ok</p>")
    }
}