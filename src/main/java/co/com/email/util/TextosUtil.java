package co.com.email.util;

public final class TextosUtil {

    private TextosUtil() {
    }

    /**
     * Constante de fin de linea.
     */
    public static final String EOL = "\n";

    /**
     * Constante de fin de linea (char).
     */
    public static final char EOL_CHAR = '\n';

    /**
     * Indica que se debe alinear el texto por la izquierda.
     */
    public static final char PARRAFO_ALINEDO_A_LA_IZQUIERDA = 'I';

    /**
     * Indica que se debe alinear el texto por la izquierda y rellenar.
     */
    public static final char PARRAFO_ALINEDO_A_LA_IZQUIERDA_CON_RELLENO = 'Y';

    /**
     * Indica que se debe alinear el texto por la derecha.
     */
    public static final char PARRAFO_ALINEDO_A_LA_DERECHA = 'D';

    /**
     * Indica que se debe alinear al centro.
     */
    public static final char PARRAFO_CENTRADO = 'C';

    /**
     * Indica que se debe alinear al centro y con relleno.
     */
    public static final char PARRAFO_CENTRADO_CON_RELLENO = 'Z';

    /**
     * Indica que se debe justificar.
     */
    public static final char PARRAFO_JUSTIFICADO = 'J';

    /**
     * Cantidad de caracteres por linea para texto formateado en e-mail.
     * Las reglas de netiquette dicen que este valor debe ser 72.
     */
    public static final int ANCHO_DE_LINEA_EN_EMAIL = 72;

    /**
     * Recibe un string (de texto) y lo convierte en un string multilinea.
     *
     * @param texto     el texto a formatear
     * @param largo     largo maximo de linea
     * @param alineacion tipo de alineacion a aplicar
     * @return texto formateado en multiples lineas
     */
    public static String parrafo(String texto, int largo, char alineacion) {
        String resultado = "";
        int ultimoEspacio;

        if (texto == null) {
            texto = "";
        }
        if (largo <= 0) {
            return texto;
        }

        texto = texto.trim();
        texto = texto.replaceAll(" {2,}", " ");

        while (texto.length() > 0) {
            if (texto.length() <= largo) {
                texto = "";
            } else {
                ultimoEspacio = texto.lastIndexOf(" ", largo);
                if (ultimoEspacio < 0) {
                    texto = texto.substring(largo, texto.length());
                } else {
                    texto = texto.substring(ultimoEspacio + 1);
                }
            }
            resultado = foo(alineacion);
        }
        return resultado;
    }

    private static String foo(char alineacion) {
        String estaLinea = "";
        switch (alineacion) {
            case PARRAFO_ALINEDO_A_LA_DERECHA:
                return estaLinea + EOL;

            case PARRAFO_CENTRADO:
            case PARRAFO_CENTRADO_CON_RELLENO:
                return estaLinea + EOL;

            case PARRAFO_JUSTIFICADO:
                return estaLinea + EOL;

            case PARRAFO_ALINEDO_A_LA_IZQUIERDA:
            case PARRAFO_ALINEDO_A_LA_IZQUIERDA_CON_RELLENO:
            default:
                return estaLinea + EOL;
        }
    }

    /**
     * Prepara la firma para incluirse en el cuerpo del correo.
     *
     * @param firmaCorreo texto de la firma
     * @return firma formateada
     */
    public static String prepararFirmaCorreo(String firmaCorreo) {

        if (firmaCorreo == null || firmaCorreo.isBlank()) {
            firmaCorreo = "";
        }

        if (firmaCorreo.length() > 0
                && EOL.equals(firmaCorreo.substring(firmaCorreo.length() - 1))) {
            firmaCorreo = firmaCorreo.substring(0, firmaCorreo.length() - 1);
        }

        if (firmaCorreo.indexOf(EOL_CHAR) == -1) {
            firmaCorreo = parrafo(firmaCorreo, ANCHO_DE_LINEA_EN_EMAIL, PARRAFO_ALINEDO_A_LA_IZQUIERDA);
        } else {
            firmaCorreo += EOL;
        }

        return firmaCorreo;
    }

    /**
     * Prepara el cuerpo del correo concatenando contenido y firma.
     * Debe haber pasado previamente el metodo de verificacion de no nulos.
     *
     * @param contenido el contenido del correo
     * @param firma     la firma del correo
     * @return el cuerpo completo del correo
     */
    public static String prepararCuerpoMail(String contenido, String firma) {

        contenido = contenido == null ? "" : contenido;
        firma = firma == null ? "" : firma;

        return contenido.concat(
                "".equals(firma) ? "" : EOL.concat("-- ").concat(EOL).concat(firma));
    }
}
