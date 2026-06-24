package co.com.email.controller;

import co.com.email.dto.CorreoSMTPRequestDto;
import co.com.email.exception.dto.ApiErrorResponse;
import co.com.email.service.NotificacionesCorreoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notificaciones Correo", description = "Operaciones de envío de correos electrónicos vía SMTP")
@RestController
@RequiredArgsConstructor
public class NotificacionesCorreoController {

    private final NotificacionesCorreoService notificacionesCorreoService;

    @Operation(
            summary = "Enviar correo SMTP",
            description = """
                    Envía un correo electrónico vía SMTP con soporte para destinatarios,
                    copia (CC), copia oculta (BCC), cuerpo en texto plano o HTML y archivos adjuntos.

                    **Requiere rol:** ADMINISTRADOR, EMPRESA, OPERARIO o FONDEADOR.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Correo enviado correctamente. Sin cuerpo de respuesta."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida. Campos requeridos ausentes o mal formados.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado. Token JWT ausente, expirado o inválido.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Sin permisos. El rol del token no tiene acceso a este endpoint.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Datos válidos pero incumplen reglas de negocio (email malformado o sin destinatarios).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping("/notificarCorreo")
    @PreAuthorize("@roleEvaluator.hasAccess(authentication, 'administracion')")
    public ResponseEntity<Void> enviarCorreoSMTP(@Validated @RequestBody CorreoSMTPRequestDto correoSMTPRequestDto) {
        notificacionesCorreoService.enviarCorreoSMTP(correoSMTPRequestDto);
        return ResponseEntity.noContent().build();
    }
}
