package com.co.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VariablesCorreoDTO {

    @Schema(name="nombre", description="Nombre de la variable del correo")
    private String nombre;

    @Schema(name="valor", description="Valor de la variable del correo")
    private String valor;


}
