package co.com.email.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpCreatedEvent {

    private String idNegociacion;
    private String rutEmpresa;
    private String rutOperador;

    private String otp;
    private LocalDateTime fechaExpiracion;
}
