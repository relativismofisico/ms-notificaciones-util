package co.com.email.kafka.consumer;

import co.com.email.domain.event.OtpCreatedEvent;
import co.com.email.service.OtpEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpNotificationConsumer {

    private final OtpEmailService otpEmailService;

    @KafkaListener(
            topics = "${topics.otp-created}",
            groupId = "notificationsGroup"
    )
    public void consume(OtpCreatedEvent event) {

        System.out.println("===== EVENTO OTP RECIBIDO =====");
        System.out.println(event);
       otpEmailService.sendOtpEmail(event);
    }
}
