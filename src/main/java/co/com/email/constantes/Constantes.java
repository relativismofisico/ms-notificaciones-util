package co.com.email.constantes;

public class Constantes {
    private Constantes() {
    }

    /**
     * Estado respuesta de error erroneo
     */
    public static final String ESTADO_RESPUESTA_EMAIL = "Estimado cliente, campo email erroneo o no existe";
    /**
     * Codigo respuesta de error erroneo
     */
    public static final String CODE_RESPUESTA_EMAIL = "500";
    /**
     * Codigo Empresa
     */
    public static final String BCI_CODE = "XXX";
    /**
     * Codigo envio Email
     */
    public static final String SRV_ENVIO_MAIL_CODE = "srvEnvioMail";
    /**
     * Codigo de retorno: indica que el asunto del correo esta vacio.
     */
    public static final String COD_ASUNTO_MAIL_VACIO = "5";
    /**
     * Mensaje de retorno: indica que el asunto del correo esta vacio.
     */
    public static final String MENSAJE_ASUNTO_MAIL_VACIO = "El asunto del correo esta vacio.";
    /**
     * Codigo de retorno: indica que la firma del correo esta vacio.
     */
    public static final String COD_FIRMA_MAIL_VACIO = "7";
    /**
     * Mensaje de retorno: indica que la firma del correo esta vacio.
     */
    public static final String MENSAJE_FIRMA_MAIL_VACIO = "La firma del correo esta vacio.";
    /**
     * Codigo de retorno: indica que la direccion de correo a procesar es erroneo
     */
    public static final String COD_DIRECCION_CORREO_ERRONEO = "10";
    /**
     * Mensaje de retorno: indica que la direccion de correo a procesar es erroneo.
     */
    public static final String MENSAJE_DIRECCION_CORREO_ERRONEO = "Direccion de correo mal estruturado.";
    /**
     * Codigo de retorno: indica que construccion del correo a fallado
     */
    public static final String COD_ERROR_CONSTRUCCION_CORREO = "1";
    /**
     * Mensaje de retorno: indica que construccion del correo a fallado
     */
    public static final String MENSAJE_ERROR_CONSTRUCCION_CORREO = "Error relacionado a la construccion del correo. ";
    /**
     * Código de validación: Parámetros no válidos (MethodArgumentNotValidException)
     */
    public static final String COD_VALIDACION_PARAMETROS_NO_VALIDOS = "VParametros";
    /**
     * Mensaje de validación: Parámetros no válidos (MethodArgumentNotValidException)
     */
    public static final String MSG_VALIDACION_PARAMETROS_NO_VALIDOS = "Uno o más parámetros no son válidos";
    /**
     * Títulos de mensajes
     */
    public static final String TITULO_PARAMETRO = "Parámetro";
    /**
     * Código títulos de mensajes
     */
    public static final String COD_TITULO_PARAMETRO = "parametro";
}
