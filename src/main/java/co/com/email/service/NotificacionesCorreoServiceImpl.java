package co.com.email.service;

import co.com.email.client.NotificaionCorreoSMTPClient;
import co.com.email.constantes.Constantes;
import co.com.email.dto.CorreoSMTPRequestDto;
import co.com.email.util.TextosUtil;
import co.com.email.util.ValidadorUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class NotificacionesCorreoServiceImpl implements NotificacionesCorreoService{

	private static final String DESTINATARIOS = "destinatarios";

	//private final List<DetalleValidacionDto> listaError = new ArrayList<>();


	/**
	 * Gestor de correos SMTP
	 */
	@Autowired
	private NotificaionCorreoSMTPClient notificacionCorreoSMTPClient;

	@Value("${cantidadLimiteDestinatarios}")
	private int cantidadLimiteDestinatarios;


	public void enviarCorreoSMTP(CorreoSMTPRequestDto correoSMTPRequestDto) {
		log.info("[NotificacionesCorreoServiceImpl][enviarCorreoSMTP] Inicio enviarCorreoSMTP: " + correoSMTPRequestDto.toString());
		validaCorreoSMTP(correoSMTPRequestDto);

		if (correoSMTPRequestDto.getNombreRemitente() == null) {
			correoSMTPRequestDto.setNombreRemitente("");
		} else {
			correoSMTPRequestDto.setNombreRemitente(correoSMTPRequestDto.getNombreRemitente().trim());
		}
		correoSMTPRequestDto.setFirma(TextosUtil.prepararFirmaCorreo(correoSMTPRequestDto.getFirma()));
		notificacionCorreoSMTPClient.enviarCorreoSMTP(correoSMTPRequestDto);
	}

	/**
	 * Cantidad límite de destinatarios en los campos To:, Cc: y Bcc:. Si se envía
	 * un correo a más destinatarios que los aquí especificados en cualquiera de los
	 * tres campos, se enviará una advertencia a la autoridad responsable. Lo mismo
	 * si la suma de las tres cantidades excede dos veces este límite.
	 *
	 * @param requestDto the request dto
	 */
	public void validarCantidadDestinatarios(CorreoSMTPRequestDto requestDto) {
		int cantDestinatariosDirectos = 0;
		int cantDestinatariosCC = 0;
		int cantDestinatariosBCC = 0;
		String destinatarios = "";
		String destinatariosCc = "";
		String destinatariosBcc = "";

		if (requestDto.getDestinatarios() != null) {
			cantDestinatariosDirectos = requestDto.getDestinatarios().size();
			destinatarios = requestDto.getDestinatarios().toString();
		}
		if (requestDto.getDestinatariosCc() != null) {
			cantDestinatariosCC = requestDto.getDestinatariosCc().size();
			destinatariosCc = requestDto.getDestinatariosCc().toString();
		}
		if (requestDto.getDestinatariosBcc() != null) {
			cantDestinatariosBCC = requestDto.getDestinatariosBcc().size();
			destinatariosBcc = requestDto.getDestinatariosBcc().toString();
		}

		Integer totalDestinatarios = cantDestinatariosDirectos + cantDestinatariosCC + cantDestinatariosBCC;

		if (totalDestinatarios <= 0) {
			log.info("No existen destinatarios, destinatariosCC o destinatariosBCC validos");
			throw new RuntimeException(Constantes.COD_VALIDACION_PARAMETROS_NO_VALIDOS);
		}

		if (cantDestinatariosDirectos > this.cantidadLimiteDestinatarios
				|| cantDestinatariosCC > this.cantidadLimiteDestinatarios
				|| cantDestinatariosBCC > this.cantidadLimiteDestinatarios
				|| totalDestinatarios > (this.cantidadLimiteDestinatarios * 2)) {

			StringBuilder msgWarn = new StringBuilder(
					"[Warning Mail] - [ Cantidad límite de destinatarios en los campos To:, Cc: y Bcc excede el limite recomendado. [");
			msgWarn.append(" \n nombreRemitente: ")
					.append(requestDto.getNombreRemitente())
					.append(" \n mailRemitente: ")
					.append(requestDto.getCorreoRemitente())
					.append(" \n Asunto: ")
					.append(requestDto.getAsunto());
			msgWarn.append(" \n Destinatarios: ").append(destinatarios);
			msgWarn.append(" \n Destinatarios CC: ").append(destinatariosCc);
			msgWarn.append(" \n Destinatarios BCC: ").append(destinatariosBcc);
			msgWarn.append(" \n cuerpoTexto: ").append(requestDto.getCuerpoTexto()).append("]");

			log.warn("[NotificacionesCorreoServiceImpl][validarCantidadDestinatarios][BCI_FINOK] metodo ejecutado con advertencia: " + msgWarn);
		}
	}

	/**
	 * Metodo que que valida los datos a enviar en el correo via SMTP
	 *
	 */
	public void validaCorreoSMTP(CorreoSMTPRequestDto requestDto) {

		List<String> empty = new ArrayList<>();

		if (!ValidadorUtil.validateEmail(requestDto.getCorreoRemitente())) {
			log.info("Parámetro correoRemitente not a well-formed email address");
			throw new RuntimeException(Constantes.COD_VALIDACION_PARAMETROS_NO_VALIDOS);
		}

		if (requestDto.getFirma() == null || requestDto.getFirma().trim().isEmpty()) {
			log.warn("[NotificacionesCorreoServiceImpl][validaCorreoSMTP]La firma es nula o vacia");
		}

		Map<Boolean, List<String>> mails;
		if (nonNull(requestDto.getDestinatarios())) {
			mails = requestDto.getDestinatarios().stream().collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
			mails.getOrDefault(Boolean.FALSE, empty).forEach(x -> log.warn("[NotificacionesCorreoServiceImpl][validaCorreoSMTP] El destinatario \"{}\" no cumple con el formato de correo ", x));
			requestDto.setDestinatarios(mails.getOrDefault(Boolean.TRUE, empty));
			mails.clear();
		}

		if (nonNull(requestDto.getDestinatariosCc())) {
			mails = requestDto.getDestinatariosCc().stream().collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
			mails.getOrDefault(Boolean.FALSE, empty).forEach(x -> log.warn("[NotificacionesCorreoServiceImpl][validaCorreoSMTP]El destinatarioCc \"{}\" no cumple con el formato de correo ", x));
			requestDto.setDestinatariosCc(mails.getOrDefault(Boolean.TRUE, empty));
			mails.clear();
		}
		if (nonNull(requestDto.getDestinatariosBcc())) {
			mails = requestDto.getDestinatariosBcc().stream().collect(Collectors.groupingBy(ValidadorUtil::validateEmail));
			mails.getOrDefault(Boolean.FALSE, empty).forEach(x -> log.warn("[NotificacionesCorreoServiceImpl][validaCorreoSMTP] El getDestinatariosBcc \"{}\" no cumple con el formato de correo ", x));
			requestDto.setDestinatariosBcc(mails.getOrDefault(Boolean.TRUE, empty));
			mails.clear();
		}
		validarCantidadDestinatarios(requestDto);
	}

}
