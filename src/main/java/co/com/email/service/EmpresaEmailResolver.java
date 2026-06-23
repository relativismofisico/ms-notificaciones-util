package co.com.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class EmpresaEmailResolver implements ActorEmailResolver {

    private final CompanyEmailService companyEmailService;

    @Override
    public boolean soporta(String tipoActor) {
        return "EMPRESA".equalsIgnoreCase(tipoActor);
    }

    @Override
    public String getEmail(String rutActor) {
        return companyEmailService.getEmailByNit(rutActor);
    }

    @Override
    public String getNombre(String rutActor) {
        return companyEmailService.getNombreByNit(rutActor);
    }
}
