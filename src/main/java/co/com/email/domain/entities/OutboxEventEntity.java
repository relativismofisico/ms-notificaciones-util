package co.com.email.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Lob;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "outbox_event")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboxEventEntity {

    @Id
    private String id;

    private String eventType;
    private String aggregateId;
    private String tipoEvento;
    private String msPropietario;

    @Lob
    private String payload;

    private Boolean enviado = false;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
    private Integer intentos = 0;
}

