package co.com.email.service;

import co.com.email.domain.entities.ConfiguracionFtp;

public interface FtpService {
    void subirArchivo(ConfiguracionFtp config, String rutaLocal, String nombreArchivo);
}
