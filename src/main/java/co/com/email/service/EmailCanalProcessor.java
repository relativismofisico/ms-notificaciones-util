package co.com.email.service;

import co.com.email.domain.event.InstruccionPagoEvent;
import co.com.email.domain.event.NotificacionEmailEvent;
import co.com.email.util.InstruccionPagoEmailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailCanalProcessor implements CanalProcessor {

    private final EmailProcessorService emailProcessorService;
    private final InstruccionPagoEmailMapper mapper;

    @Override
    public boolean soporta(String canal) {
        return "EMAIL".equalsIgnoreCase(canal);
    }

    @Override
    public void procesar(InstruccionPagoEvent event) {

        NotificacionEmailEvent emailEvent =
                mapper.toEmailEvent(event);

        emailProcessorService.procesar(emailEvent);
    }
}
