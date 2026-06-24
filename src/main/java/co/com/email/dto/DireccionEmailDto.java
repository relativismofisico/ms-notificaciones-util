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
@Schema(description = "Dirección de correo electrónico de un destinatario")
public class DireccionEmailDto {

    @Schema(
            description = "Dirección de correo electrónico",
            example = "usuario@empresa.com"
    )
    private String email;
}
