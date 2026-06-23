package co.com.email.service;

import co.com.email.domain.entities.ConfiguracionFtp;
import co.com.email.domain.event.InstruccionPagoEvent;
import co.com.email.repositories.FtpConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FtpCanalProcessor implements CanalProcessor {

    private final FtpConfiguracionRepository ftpRepository;
    private final FtpService ftpService;

    @Override
    public boolean soporta(String canal) {
        return "FTP".equalsIgnoreCase(canal);
    }

    @Override
    public void procesar(InstruccionPagoEvent event) {
        try {

            log.info("📤 Procesando envío FTP para fondeador: {}", event.getFondeadorId());

            ConfiguracionFtp config =
                    ftpRepository.findByEmpresa(event.getFondeadorId())
                            .orElseThrow(() ->
                                    new RuntimeException("No existe configuración FTP para empresa "
                                            + event.getFondeadorId())
                            );

            ftpService.subirArchivo(
                    config,
                    event.getRutaArchivo(),
                    event.getNombreArchivo()
            );

        } catch (Exception e) {
            log.error("❌ Error en envío FTP", e);
            throw e;
        }
    }

}
