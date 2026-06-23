package co.com.email.service;

import co.com.email.domain.event.InstruccionPagoEvent;

public interface CanalProcessor {

    boolean soporta(String canal);

    void procesar(InstruccionPagoEvent event);
}
