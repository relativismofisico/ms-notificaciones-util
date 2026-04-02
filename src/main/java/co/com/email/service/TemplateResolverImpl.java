package co.com.email.service;

import co.com.email.domain.event.NotificacionEmailEvent;
import org.springframework.stereotype.Service;

@Service
public class TemplateResolverImpl implements TemplateResolver {

    @Override
    public EmailTemplate resolve(NotificacionEmailEvent event) {

        if (event.getTipoNotificacion() == null) {
            throw new IllegalArgumentException("tipoNotificacion es null");
        }

        switch (event.getTipoNotificacion()) {

            case "NEGOCIACION_CREADA":
                return new NegociacionCreadaTemplate();

            default:
                throw new RuntimeException("No existe plantilla para tipo: "
                        + event.getTipoNotificacion());
        }
    }
}
