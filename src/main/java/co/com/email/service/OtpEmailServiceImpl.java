package co.com.email.service;

import co.com.email.domain.entities.MessageTemplate;
import co.com.email.domain.event.OtpCreatedEvent;
import co.com.email.repositories.MessageTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpEmailServiceImpl implements OtpEmailService {

    private final MessageTemplateRepository templateRepository;
    private final UserEmailService userEmailService;
    private final EmailSenderService emailSenderService;


    @Override
    public void sendOtpEmail(OtpCreatedEvent event) {

        //1. username = rutOperador
        String username = event.getRutOperador();

        String email = userEmailService.getEmailByUsername(username);
        String nombre = userEmailService.getFullNameByUsername(username);

        MessageTemplate template = templateRepository
                .findByTemplateName("otp")
                .orElseThrow(() -> new RuntimeException("Template OTP no encontrado"));

        String html = template.getContent()
                .replace("{{nombre}}", nombre)
                .replace("{{otp}}", event.getOtp());

        emailSenderService.sendHtmlEmail(email, template.getEmailSubject(), html);

    }
}
