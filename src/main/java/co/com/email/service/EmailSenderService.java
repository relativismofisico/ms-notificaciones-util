package co.com.email.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendHtmlEmail(String to, String subject, String html) {

        MimeMessage message = mailSender.createMimeMessage();

        try {


            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);


            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo", e);
        }
    }

    public void sendHtmlEmailWithAttachment(
            String to,
            String subject,
            String html,
            String filePath,
            String fileName
    ) {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));

            if (file.exists()) {
                helper.addAttachment(fileName, file);
            }

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando email con adjunto", e);
        }
    }
}