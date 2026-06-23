package co.com.email.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ftp_configuracion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracionFtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String empresa;

    @Column(name = "host_fingerprint", length = 512)
    private String hostFingerprint;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private int puertoFtp;

    @Column(nullable = false)
    private String usuarioFtp;

    private String contrasenaFtp;
    private String privateKeyPath;
    private String passphrase;

    @Column(nullable = false)
    private String remoteDir;

    @Column(nullable = false)
    private String rutaProcesados;

    @Column(nullable = false)
    private String rutaErrores;

    @Column(nullable = false)
    private String tipoDeAutenticacion;

    @Column(name = "usuario_aplicacion")
    private String usuarioAplicacion;
}
