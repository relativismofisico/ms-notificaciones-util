package com.co.email;


import com.co.email.constantes.Constantes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionManager {

    @Autowired(required = false)
    private MessageSource messageSource;

    private static final ThreadLocal<Locale> localeTL = new ThreadLocal<>();

    /**
     * Metodo encargado de tratar excepcion MethodArgumentNotValidException. <br>
     * Retorna siempre un response con el código
     * Constantes#COD_VALIDACION_PARAMETROS_NO_VALIDOS y mensaje
     * Constantes#MSG_VALIDACION_PARAMETROS_NO_VALIDOS <br>
     * Además se incluye el detalle de parámetros de entrada y validaciones no
     * exitosas
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<Object> manageMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        log.error("Llamada a endpoint [%s] método [%s]: Cuerpo de mensaje posee uno o más campos no válidos", e);

        String mensaje;
        if (messageSource == null) {
            mensaje = Constantes.MSG_VALIDACION_PARAMETROS_NO_VALIDOS;
        } else {
            mensaje = messageSource.getMessage(
                    "exceptionmanager.methodargumentnotvalid."
                            .concat(Constantes.COD_VALIDACION_PARAMETROS_NO_VALIDOS).concat(".mensaje"),
                    null, Constantes.MSG_VALIDACION_PARAMETROS_NO_VALIDOS, getCurrentLocale());
        }

        log.error(" Llamada a endpoint [%s] método [%s]: Cuerpo de mensaje posee uno o más campos no válidos",
                validateLoggerInput(canonicalize(request.getRequestURI())),
                validateLoggerInput(canonicalize(request.getMethod())));

        return new ResponseEntity<>("", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    private String validateLoggerInput(String texto) {
        return texto != null ? texto.replaceAll("[^a-zA-Z0-9._+/-]", "") : "";
    }

    private String canonicalize(String prevalidatedStr) {
        try {
            if (prevalidatedStr != null) {
                return new String(prevalidatedStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Obtiene el Locale.
     *
     * @return Objeto Locale
     */
    private static Locale getCurrentLocale() {
        return localeTL.get();
    }

}