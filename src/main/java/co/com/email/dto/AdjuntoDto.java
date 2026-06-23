package co.com.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdjuntoDto {

	//Corresponde al nombre del archivo adjunto
	private String nombreArchivo;
	
	//Corresponde a la extension del archivo adjunto
	private String extension;
	
	//Corresponde a la representancion en base 64 de un archivo
	private String archivoBase64;
}
