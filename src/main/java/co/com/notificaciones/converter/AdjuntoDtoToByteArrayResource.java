package co.com.notificaciones.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ByteArrayResource;

import co.com.notificaciones.dto.AdjuntoDto;
import java.util.Base64;

public class AdjuntoDtoToByteArrayResource implements Converter<AdjuntoDto, ByteArrayResource>{

	@Override
	public ByteArrayResource convert(AdjuntoDto source) {
		Base64.Decoder decoder = Base64.getDecoder();
		return new ByteArrayResource(decoder.decode(source.getArchivoBase64()));
	}
	

}
