package co.com.email.service;

import co.com.email.domain.event.NotificacionEmailEvent;

public interface EmailProcessorService {
    void procesar(NotificacionEmailEvent event);
}
