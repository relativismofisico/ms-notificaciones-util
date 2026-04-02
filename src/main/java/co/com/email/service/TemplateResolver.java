package co.com.email.service;

import co.com.email.domain.event.NotificacionEmailEvent;

public interface TemplateResolver {
    EmailTemplate resolve(NotificacionEmailEvent event);
}
