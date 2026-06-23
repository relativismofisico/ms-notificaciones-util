package co.com.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class UsuarioEmailResolver implements ActorEmailResolver {

    private final UserEmailService userEmailService;

    @Override
    public boolean soporta(String tipoActor) {
        return !"EMPRESA".equalsIgnoreCase(tipoActor);
    }

    @Override
    public String getEmail(String rutActor) {
        return userEmailService.getEmailByUsername(rutActor);
    }

    @Override
    public String getNombre(String rutActor) {
        return userEmailService.getFullNameByUsername(rutActor);
    }
}