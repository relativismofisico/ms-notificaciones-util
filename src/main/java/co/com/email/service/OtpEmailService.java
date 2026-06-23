package co.com.email.service;

import co.com.email.domain.entities.Person;
import co.com.email.domain.event.OtpCreatedEvent;

public interface OtpEmailService {
    void sendOtpEmail(OtpCreatedEvent event);
}
