package co.com.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de envío de correo electrónico vía SMTP")
public class CorreoSMTPRequestDto {

    @Schema(
            description = "Nombre visible del remitente en el cliente de correo",
            example = "Plataforma Factoring"
    )
    private String nombreRemitente;

    @NotNull
    @NotEmpty
    @Schema(
            description = "Dirección de correo electrónico del remitente",
            example = "notificaciones@factoring.co.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String correoRemitente;

    @NotNull
    @NotEmpty
    @Schema(
            description = "Asunto del correo electrónico",
            example = "Instrucción de pago #INS-2024-001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String asunto;

    @NotNull
    @NotEmpty
    @Schema(
            description = "Lista de direcciones de correo de los destinatarios principales",
            example = "[\"empresa@cliente.com\", \"contacto@proveedor.com\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<String> destinatarios;

    @Schema(
            description = "Lista de direcciones en copia (CC). Opcional.",
            example = "[\"supervisor@factoring.co.com\"]"
    )
    private List<String> destinatariosCc;

    @Schema(
            description = "Lista de direcciones en copia oculta (BCC). Opcional.",
            example = "[\"auditoria@factoring.co.com\"]"
    )
    private List<String> destinatariosBcc;

    @Schema(
            description = "Cuerpo del correo en texto plano. Se usa cuando el cliente no soporta HTML.",
            example = "Estimado cliente, su instrucción de pago ha sido procesada."
    )
    private String cuerpoTexto;

    @Schema(
            description = "Cuerpo del correo en formato HTML. Tiene prioridad sobre cuerpoTexto si ambos están presentes.",
            example = "<h1>Instrucción procesada</h1><p>Estimado cliente...</p>"
    )
    private String cuerpoHtml;

    @Schema(
            description = "Firma que se agrega al pie del correo",
            example = "Equipo Factoring | www.factoring.co.com"
    )
    private String firma;

    @Schema(
            description = "Lista de archivos adjuntos codificados en Base64. Opcional."
    )
    private List<AdjuntoDto> adjuntos;

    @Override
    public String toString() {
        return "CorreoSMTPRequestDto{" +
                "nombreRemitente='" + nombreRemitente + '\'' +
                ", correoRemitente='" + correoRemitente + '\'' +
                ", asunto='" + asunto + '\'' +
                ", destinatarios='" + destinatarios +
                '}';
    }
}
