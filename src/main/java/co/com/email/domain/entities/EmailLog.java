package co.com.email.domain.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "email_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {

    @Id
    private String id;

    private String destinatario;
    private String asunto;
    private String estado; // ENVIADO / ERROR
    private String error;
    private LocalDateTime fecha;
}
