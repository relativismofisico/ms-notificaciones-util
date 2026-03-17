package co.com.email.service;

import co.com.email.domain.entities.MessageTemplate;
import co.com.email.domain.entities.Person;
import co.com.email.domain.entities.User;
import co.com.email.domain.event.OtpCreatedEvent;
import co.com.email.repositories.MessageTemplateRepository;
import co.com.email.repositories.PersonRepository;
import co.com.email.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpEmailServiceImpl implements OtpEmailService {

    private final MessageTemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final JavaMailSender mailSender;
    private final OutboxService outboxService;

    @Value("${topics.otp-email-sent}")
    private String otpEmailSentTopic;

    @Override
    public void sendOtpEmail(OtpCreatedEvent event) {

        //1. username = rutOperador
        String username = event.getRutOperador();

        //2. Buscar usuario
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //3. Obtener person_IDE
        Long personIde = user.getPerson();

        //4. Buscar Persona
        Person person = personRepository
                .findById(personIde)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        //5. Obtener email
        String email = person.getEmail();

        MessageTemplate template = templateRepository
                .findByTemplateName("otp")
                .orElseThrow(() -> new RuntimeException("Template OTP no encontrado"));

        String nombre = person.getFirstName() + " " + person.getFirstLastname();

        String html = template.getContent()
                .replace("{{nombre}}", nombre)
                .replace("{{otp}}", event.getOtp());

        sendEmail(email, template.getEmailSubject(), html);

    }

    private void sendEmail(String to, String subject, String html) {

        MimeMessage message = mailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo OTP", e);
        }
    }
}
