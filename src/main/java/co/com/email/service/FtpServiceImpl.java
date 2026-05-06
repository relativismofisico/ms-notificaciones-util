package co.com.email.service;

import co.com.email.domain.entities.ConfiguracionFtp;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class FtpServiceImpl implements FtpService {

    @Override
    public void subirArchivo(ConfiguracionFtp config, String rutaLocal, String nombreArchivo) {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();

            // 🔐 AUTENTICACIÓN POR KEY
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

            // 🔐 PASSWORD
            if ("PASSWORD".equalsIgnoreCase(config.getTipoDeAutenticacion())) {
                session.setPassword(config.getContrasenaFtp());
            }

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            channelSftp = (ChannelSftp) channel;

            // 📂 Ir al directorio remoto
            channelSftp.cd(config.getRemoteDir());

            File file = new File(rutaLocal);

            log.info("📤 Subiendo archivo {} a {}", nombreArchivo, config.getRemoteDir());

            channelSftp.put(file.getAbsolutePath(), nombreArchivo);

            log.info("✅ Archivo subido correctamente");

        } catch (Exception e) {
            log.error("❌ Error subiendo archivo por SFTP", e);
            throw new RuntimeException(e);
        } finally {
            if (channelSftp != null) channelSftp.exit();
            if (session != null) session.disconnect();
        }
    }
}
