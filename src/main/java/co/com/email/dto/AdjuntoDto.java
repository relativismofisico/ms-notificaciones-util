package co.com.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Archivo adjunto codificado en Base64 para incluir en el correo")
public class AdjuntoDto {

    @Schema(
            description = "Nombre del archivo adjunto sin extensión",
            example = "factura_001"
    )
    private String nombreArchivo;

    @Schema(
            description = "Extensión del archivo adjunto (sin punto)",
            example = "pdf",
            allowableValues = {"pdf", "xlsx", "csv", "docx", "png", "jpg"}
    )
    private String extension;

    @Schema(
            description = "Contenido del archivo codificado en Base64",
            example = "JVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3Ro..."
    )
    private String archivoBase64;
}
