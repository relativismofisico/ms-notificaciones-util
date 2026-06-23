package co.com.email.service;

public interface ActorEmailResolver {

    boolean soporta(String tipoActor);

    String getEmail(String rutActor);

    String getNombre(String rutActor);
}
