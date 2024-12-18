package co.com.email.controller;

import co.com.email.dto.CorreoSMTPRequestDto;
import co.com.email.service.NotificacionesCorreoService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@OpenAPIDefinition(
        info = @Info(
                description = "Microservicio para notificaciones de correos y mensajeria de texto",
                version = "1.0.0",
                title = "ms-notificaciones-util"
        ),
        servers = {
                @Server(url = "Direccion del servidor en desarrollo/ms-notificacionescorreo-util", description = "Develop server"),
                @Server(url = "http://localhost:8080", description = "Local server"),
                @Server(url = "Direccion del servidor en produccion", description = "Production server")
        }
)

@RestController
public class NotificacionesCorreoController {

    @Autowired
    private NotificacionesCorreoService notificacionesCorreoService;

    /**
     * Servicio de notificacion correo
     */

    @Operation(summary = "Operacion que permite el envio de correos SMTP ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content", headers = {
                    @Header(name = HttpHeaders.CONTENT_TYPE, description = "Content Type Header", schema = @Schema(implementation = String.class))
            })
    })
    @PostMapping(value = "/notificarCorreo")
    public ResponseEntity<?> enviarCorreoSMTP(@Validated @RequestBody CorreoSMTPRequestDto correoSMTPRequestDto) {
        notificacionesCorreoService.enviarCorreoSMTP(correoSMTPRequestDto);
        return ResponseEntity.noContent().build();
    }
}
