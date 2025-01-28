package com.co.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CorreoSMTPRequestDto {

    @Schema(name="tipoCorreo", description="Número que indica que clase de correo es el que se debe de enviar")
    private Long tipoCorreo;

    @Schema(name="nombreRemitente", description="Nombre del remitente del correo electronico")
    private String nombreRemitente;

    //SE BORRA
    @NotNull
    @NotEmpty
    @Schema(name="remitente", description="direccion email del remitente del correo electronico")
    private String correoRemitente;

    @Schema(name="nombreReceptor", description="Nombre del que recibe el correo")
    private String nombreReceptor;

    //DEPENDE DEL CODIGO DE LA PLANTILLA ENVIADA
    @NotNull
    @NotEmpty
    @Schema(name="asunto", description="asunto del correo electronico")
    private String asunto;

    @NotNull
    @NotEmpty
    @Schema(name="destinatarios", description="listado de emails de destinatarios")
    private List<String> destinatarios;

    @Schema(name="destinatariosCc", description="listado de emails de destinatarios con Copia")
    private List<String> destinatariosCc;

    @Schema(name="destinatariosBcc", description="listado de emails de destinatarios con copia oculta")
    private List<String> destinatariosBcc;

    @Schema(name="cuerpoTexto", description="Corresponde al texto ubicado en el cuerpo del correo")
    private String cuerpoTexto;

    @Schema(name="cuerpoHTML", description="Corresponde al codigo HTML a ubicarse  en el cuerpo del correo")
    private String cuerpoHtml = "newCustomer.html";

    //SE BORRA, Se debe sacar de una tabla y dependa de la plantilla
    @Schema(name="firma", description="texto usado para firmar el correo")
    private String firma;

    @Schema(name="adjuntos", description="Archivos adjuntos que se enviaran junto al correo")
    private List<AdjuntoDto> adjuntos;

    @Schema(name="variables", description="Variables con su nombre y valor para el correo")
    private List<VariablesCorreoDTO> variables;

    @Override
    public String toString(){
        return "CorreoSMTPRequestSto{" +
                "nombreRemitente='" + nombreRemitente + '\'' +
                ", correoRemitente='" + correoRemitente + '\'' +
                ", asunto='" + asunto + '\'' +
                ", destinatarios='" + destinatarios +
                '}';
    }
    //Se piensa para link de verificaciones
   // private List<Offer> offerings;
}
