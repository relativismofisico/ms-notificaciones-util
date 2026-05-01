package co.com.email.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstruccionPagoEvent {

    private String instruccionId;
    private String fondeadorId;
    private String pagadorId;
    private String pagadorNombre;

    private Integer cantidadFacturas;
    private BigDecimal totalPagar;
    private BigDecimal netoGiroCliente;

    private String nombreArchivo;
    private String rutaArchivo;

    private Instant fechaGeneracion;
}

