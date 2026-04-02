package co.com.email.service;

public interface EmailSender {
    void enviar(String to, String subject, String body);
}
