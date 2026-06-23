package co.com.email.service;

import co.com.email.domain.entities.ConfiguracionFtp;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class FtpServiceImpl implements FtpService {

    @Override
    public void subirArchivo(ConfiguracionFtp config, String rutaLocal, String nombreArchivo) {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();

            if ("KEY".equalsIgnoreCase(config.getTipoDeAutenticacion())) {
                jsch.addIdentity(
                        config.getPrivateKeyPath(),
                        config.getPassphrase()
                );
            }

            session = jsch.getSession(
                    config.getUsuarioFtp(),
                    config.getHost(),
                    config.getPuertoFtp()
            );

            if ("PASSWORD".equalsIgnoreCase(config.getTipoDeAutenticacion())) {
                session.setPassword(config.getContrasenaFtp());
            }

            if (config.getHostFingerprint() != null && !config.getHostFingerprint().isBlank()) {
                session.setConfig("StrictHostKeyChecking", "yes");
                session.setConfig("FingerprintHash", "sha256");
                jsch.setKnownHosts(new ByteArrayInputStream(
                        (config.getHost() + " " + config.getHostFingerprint())
                                .getBytes(StandardCharsets.UTF_8)));
            } else {
                session.setConfig("StrictHostKeyChecking", "no");
                log.warn("SFTP host key verification disabled for {}. Configure hostFingerprint to enable it.",
                        config.getHost());
            }
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            channelSftp = (ChannelSftp) channel;

            channelSftp.cd(config.getRemoteDir());

            File file = new File(rutaLocal);

            log.info("Subiendo archivo {} a {}", nombreArchivo, config.getRemoteDir());

            channelSftp.put(file.getAbsolutePath(), nombreArchivo);

            log.info("Archivo subido correctamente");

        } catch (Exception e) {
            log.error("Error subiendo archivo por SFTP", e);
            throw new RuntimeException(e);
        } finally {
            if (channelSftp != null) {
                channelSftp.exit();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
