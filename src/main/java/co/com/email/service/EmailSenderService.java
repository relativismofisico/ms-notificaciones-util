package co.com.email.service;

public interface EmailSenderService {

    void sendHtmlEmail(String to, String subject, String html);

    void sendHtmlEmailWithAttachment(String to, String subject, String html,
                                     String filePath, String fileName);
}
