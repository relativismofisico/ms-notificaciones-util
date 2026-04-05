package co.com.email.kafka.consumer;

import co.com.email.domain.event.OtpCreatedEvent;
import co.com.email.repositories.OutboxEventRepository;
import co.com.email.service.OtpEmailService;
import co.com.email.service.OutboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpNotificationConsumer {

    private final OtpEmailService otpEmailService;
    private final OutboxService outboxService;

    @Value("${topics.otp-email-sent}")
    private String otpEmailSentTopic;


    @KafkaListener(
            topics = "${topics.otp-created}",
            groupId = "notificationsGroup"
    )
    public void consume(OtpCreatedEvent event) {

        System.out.println("===== EVENTO OTP RECIBIDO =====");
        System.out.println(event);
        //otpEmailService.sendOtpEmail(event);
        // guardar evento en outbox
        outboxService.guardarEvento(
                event.getIdNegociacion(),
                otpEmailSentTopic,
                event
        );
    }
}
