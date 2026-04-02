package co.com.email.domain.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionEmailEvent {

    private String idEvento;
    private String idNegociacion;
    private String tipoNotificacion;
    private List<DestinatarioEmail> destinatarios;
    private String asunto;
    private Map<String, Object> variables;
    private LocalDateTime fecha;
}
