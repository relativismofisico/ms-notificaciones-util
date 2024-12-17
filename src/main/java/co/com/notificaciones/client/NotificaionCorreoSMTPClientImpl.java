package co.com.notificaciones.client;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import co.com.notificaciones.dto.AdjuntoDto;
import co.com.notificaciones.dto.CorreoSMTPRequestDto;
import co.com.notificaciones.util.TextosUtil;
import jakarta.mail.Message;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class NotificaionCorreoSMTPClientImpl implements NotificaionCorreoSMTPClient{
	
	@Autowired
	private JavaMailSender javaMailSender;

	
	/**
	 * Metodo para el envio de correo utilizando protocolo SMTP
	 */
	@Override
	public void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto) {

		MimeMessage mimeMail = javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMail, true);
			
			mimeMail.setFrom(convertInternetAddress(correoSMTPRequestDto.getCorreoRemitente(), correoSMTPRequestDto.getNombreRemitente()));
			mimeMail.addRecipients(Message.RecipientType.TO,
					convertListStringToInternetAddressArray(correoSMTPRequestDto.getDestinatarios()));
			mimeMail.setSubject(correoSMTPRequestDto.getAsunto());

			mimeMail.addRecipients(Message.RecipientType.CC,
					convertListStringToInternetAddressArray(correoSMTPRequestDto.getDestinatariosCc()));

			mimeMail.addRecipients(Message.RecipientType.BCC,
					convertListStringToInternetAddressArray(correoSMTPRequestDto.getDestinatariosBcc()));


			if (correoSMTPRequestDto.getCuerpoHtml() != null && !correoSMTPRequestDto.getCuerpoHtml().equals("")) {

				StringBuilder contenidoCorreo = new StringBuilder("<div>").append(correoSMTPRequestDto.getCuerpoTexto())
						.append("</div>").append("<br/>").append(correoSMTPRequestDto.getCuerpoHtml()).append("<br/>")
						.append("<div>").append(correoSMTPRequestDto.getFirma()).append("</div>");

				mimeHelper.setText(contenidoCorreo.toString(),true);
				
			} else {
				mimeHelper.setText(TextosUtil.prepararCuerpoMail(correoSMTPRequestDto.getCuerpoTexto(),
						correoSMTPRequestDto.getFirma()));
			}

			agregarAdjuntos(correoSMTPRequestDto.getAdjuntos(), mimeHelper);
			mimeMail.setSentDate(new Date());

			javaMailSender.send(mimeMail);
		}catch (Exception e) {
			log.error(("Error en la conexion con servidor SMTP"), e);
		} 
	}

	private void agregarAdjuntos(List<AdjuntoDto> adjuntos, MimeMessageHelper mimeHelper) {
		if (null != adjuntos) {
			adjuntos.forEach(adjunto -> {				
				try {
					mimeHelper.addAttachment(adjunto.getNombreArchivo(),new ByteArrayResource(Base64.getDecoder().decode(adjunto.getArchivoBase64())));
				} catch (Exception e) {
					log.error(("[MessagingException] Error al adjuntar archivo: " + e.getMessage()));
				}
			});
		}
	}

	/**
	 * Convert list to internet address array internet address [ ].
	 *
	 * @param direcciones the direcciones
	 * @return the internet address [ ]
	 * @throws AddressException 
	 */
	private InternetAddress[] convertListStringToInternetAddressArray(List<String> direcciones) throws AddressException {
		if (direcciones != null) {
			InternetAddress[] arregloDirecciones = new InternetAddress[direcciones.size()];
			for (int i = 0; i < direcciones.size(); i++) {
				arregloDirecciones[i] = convertInternetAddress(direcciones.get(i));
			}
			return arregloDirecciones;
		} else {
			return new InternetAddress[0];
		}
	}

	private InternetAddress convertInternetAddress(String direccion) throws AddressException {
		try {
			return new InternetAddress(direccion);
		} catch (AddressException e) {
			throw new AddressException("500","Error al convertir InternetAddress : " + e.getMessage());
		}
	}

	private InternetAddress convertInternetAddress(String direccion, String personal) throws UnsupportedEncodingException {
		return new InternetAddress(direccion, personal);
	}

}
