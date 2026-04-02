package co.com.email.service;

import co.com.email.domain.event.NotificacionEmailEvent;

public interface EmailTemplate {
    String render(NotificacionEmailEvent event);
}
