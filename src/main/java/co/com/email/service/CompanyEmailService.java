package co.com.email.service;

import co.com.email.domain.entities.Company;
import co.com.email.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyEmailService {

    private final CompanyRepository companyRepository;

    public String getEmailByNit(String nit) {
        return companyRepository.findByNit(nit)
                .map(Company::getEmailCompany)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada: " + nit));
    }

    public String getNombreByNit(String nit) {
        return companyRepository.findByNit(nit)
                .map(Company::getNameCompany)
                .orElse("EMPRESA");
    }
}
