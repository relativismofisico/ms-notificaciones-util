package co.com.email.kafka.consumer;

import co.com.email.domain.event.OtpCreatedEvent;
import co.com.email.service.OtpEmailService;
import co.com.email.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
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
        log.info("Evento OTP recibido para negociación: {}", event.getIdNegociacion());
        outboxService.guardarEvento(
                event.getIdNegociacion(),
                otpEmailSentTopic,
                event
        );
    }
}
