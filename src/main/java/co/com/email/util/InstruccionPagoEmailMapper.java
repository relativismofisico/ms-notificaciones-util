package co.com.email.util;

import co.com.email.domain.event.DestinatarioEmail;
import co.com.email.domain.event.InstruccionPagoEvent;
import co.com.email.domain.event.NotificacionEmailEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class InstruccionPagoEmailMapper {

    public NotificacionEmailEvent toEmailEvent(InstruccionPagoEvent event) {

        return NotificacionEmailEvent.builder()
                .asunto("INSTRUCCIONES_PAGO_GENERADAS")
                .destinatarios(List.of(
                        DestinatarioEmail.builder()
                                .tipoActor("EMPRESA")
                                .rutActor(event.getFondeadorId())
                                .build()
                ))
                .data(Map.of(
                        "pagador", event.getPagadorNombre(),
                        "cantidadFacturas", event.getCantidadFacturas(),
                        "totalPagar", event.getTotalPagar(),
                        "archivo", event.getNombreArchivo(),
                        "rutaArchivo", event.getRutaArchivo()
                ))
                .build();
    }
}
