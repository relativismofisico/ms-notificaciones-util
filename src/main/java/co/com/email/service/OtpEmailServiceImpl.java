package co.com.email.service;

import co.com.email.domain.event.OtpCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OtpEmailServiceImpl implements OtpEmailService {

    private final UserEmailService userEmailService;
    private final EmailSenderService emailSenderService;
    private final TemplateService templateService;

    @Override
    public void sendOtpEmail(OtpCreatedEvent event) {
        String username = event.getRutOperador();

        String email = userEmailService.getEmailByUsername(username);
        String nombre = userEmailService.getFullNameByUsername(username);

        Map<String, Object> data = Map.of(
                "nombre", nombre,
                "otp", event.getOtp()
        );

        String html = templateService.build("otp", data);
        String subject = templateService.getSubject("otp");

        emailSenderService.sendHtmlEmail(email, subject, html);
    }
}
