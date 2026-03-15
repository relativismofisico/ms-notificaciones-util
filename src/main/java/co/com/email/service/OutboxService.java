package co.com.email.service;

public interface OutboxService {
    void guardarEvento(String aggregateId, String tipoEvento, Object payload);
}
