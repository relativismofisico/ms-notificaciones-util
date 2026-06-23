package co.com.email.repositories;

import co.com.email.domain.entities.ConfiguracionFtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FtpConfiguracionRepository extends JpaRepository<ConfiguracionFtp, Long> {
    Optional<ConfiguracionFtp> findByEmpresa(String empresa);
    void deleteByEmpresa(String empresa);

}
