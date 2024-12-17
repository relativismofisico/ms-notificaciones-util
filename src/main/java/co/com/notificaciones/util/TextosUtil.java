package co.com.notificaciones.util;

import ch.qos.logback.core.util.StringUtil;

public class TextosUtil {
	
	private TextosUtil() {
	}
	
	/**
	 * Constantes de fin de linea. <B> NO TOCAR </B>*/
	public static String EOL="\n";
	
	/**
	 * Constantes de fin de linea. <B> NO TOCAR </B>*/
	public static char EOLchar='\n';
	
	/**
	 * Constantes para los metodos parrafo
	 * Indica que se debe alinear el texto por la izquierda. <B> NO TOCAR </B>*/
	public static final char PARRAFO_ALINEDO_A_LA_IZQUIERDA='I';
	
	/**
	 * Constantes para los metodos parrafo
	 * Indica que se debe alinear el texto por la izquierda y rellenar. <B> NO TOCAR </B>*/
	public static final char PARRAFO_ALINEDO_A_LA_IZQUIERDA_CON_RELLENO='Y';
	
	/**
	 * Constantes para los metodos parrafo
	 * Indica que se debe alinear el texto por la derecha. <B> NO TOCAR </B>*/
	public static final char PARRAFO_ALINEDO_A_LA_DERECHA='D';
	
	/**
	 * Constantes para los metodos parrafo
	 * Indica que se debe alinear al centro. <B> NO TOCAR </B>*/
	public static final char PARRAFO_CENTRADO='C';
	
	/**
	 * Constantes para los metodos parrafo
	 * Indica que se debe alinear al centro y con relleno. <B> NO TOCAR </B>*/
	public static final char PARRAFO_CENTRADO_CON_RELLENO='Z';
	
	/**
	 * Constantes para los metodos parrafo
	 * Indica que se debe justificar. <B> NO TOCAR </B>*/
	public static final char PARRAFO_JUSTIFICADO='J';
	
	/**
     * Constante que determina la cantidad de caracteres por linea que debe
     * tener un texto formateado para e-mail. Las reglas de
     * <EM>netiquette</EM> dicen que este valor debe ser 72.
     */
    public static final int ANCHO_DE_LINEA_EN_EMAIL=72; /* PROHIBIDO CAMBIAR ESTE VALOR */
	
    /**
     * Recibe un string (de texto) y lo convierte en un string multilinea
     * (cada linea separada por un EOL), en que cada linea tiene a lo mas el
     * largo especificado y esta alineada segun se especifique. El proposito
     * de este metodo es facilitar el formateo de parrafos de texto,
     * principalmente para la composicion de textos que se enviaran por
     * correo electronico.
     * <p>
     * Notas:<ul>
     * <li>Si una palabra (entendida aqui como una secuencia de caracteres
     *     que no son espacio) tiene un largo mayor que el largo de linea
     *     especificado, se <b>cortara</b> sin misericordia para evitar que
     *     hayan lineas mas largas de lo debido. El remanente ira a la
     *     siguiente linea.<p>
     * <li>El metodo <B>compacta</B> secuencias de varios espacios en uno
     *     solo y elimina los de los extremos; esto es, reduce <PRE>
     *     " Hola   que tal?    Festival! " a "Hola que tal? Festival!"
     *     </PRE>
     *     Esto es intencional, pues es necesario para poder alinear bien los
     *     textos que se entreguen. La implementacion base anterior (1.1A de
     *     {@link #parrafo(String, int)}) NO lo hacia (con el errado
     *     proposito de no estropear posibles tabulaciones previas, cosa que
     *     no tiene mucho sentido cuando hablamos de strings de TEXTO). El
     *     haber efectuado este cambio no afecta al codigo fuente previamente
     *     existente, por lo que no se introducen problemas.<p>
     * <li>Por construccion, los parrafos armados con las alineaciones
     *     {@link #PARRAFO_ALINEADO_A_LA_DERECHA} y
     *     {@link #PARRAFO_JUSTIFICADO} tienen lineas que contienen cada una
     *     el largo especificado, mientras que con
     *     {@link #PARRAFO_ALINEADO_A_LA_IZQUIERDA} y
     *     {@link #PARRAFO_CENTRADO}, las lineas tienen <b>a lo mas</B> el
     *     largo especificado. Si hubiere una situacion en que se necesitare
     *     que cada linea alcanzare siempre el largo especificado, se pueden
     *     usar las alineaciones
     *     {@link #PARRAFO_ALINEADO_A_LA_IZQUIERDA_CON_RELLENO} y
     *     {@link #PARRAFO_CENTRADO_CON_RELLENO}, que hacen lo mismo que sus
     *     analogas y ademas aniaden espacios al final de cada linea.<p>
     * </ul><p>
     *
     * Registro de versiones:<ul>
     * <li>1.0 18/04/2006 Miguel Farah (BCI): version inicial.
     * </ul><p>
     *
     * @param texto El texto a convertir en un parrafo.<p>
     *
     * @param largo El largo maximo de cada linea del parrafo. Si se entrega
     *              un largo nulo o negativo, se retorna el texto sin
     *              modificaciones.<p>
     *
     * @param alineacion La alineacion del parrafo; se reconocen los valores
     *                   {@link #PARRAFO_ALINEADO_A_LA_IZQUIERDA},
     *                   {@link #PARRAFO_ALINEADO_A_LA_IZQUIERDA_CON_RELLENO},
     *                   {@link #PARRAFO_ALINEADO_A_LA_DERECHA},
     *                   {@link #PARRAFO_CENTRADO},
     *                   {@link #PARRAFO_CENTRADO_CON_RELLENO} y
     *                   {@link #PARRAFO_JUSTIFICADO}; si se entrega un
     *                   valor invalido, se reemplazara por el primero.<p>
     *
     * @return Un string multilinea, <code>""</code> en el peor caso. Nunca
     *         se retorna <code>null</code>.<p>
     *
     * @since  1.1
     */
    public static String parrafo(String texto, int largo, char alineacion) {
        String resultado="";
        int ultimoEspacio;

        if (texto==null) texto="";
        if (largo<=0) return texto;

        // Antes que nada, me deshago de los espacios de mas.
        texto=texto.trim();
        while (texto.contains("  ")) { texto=StringUtil.capitalizeFirstLetter(resultado); }

        while (texto.length()>0) {
            if (texto.length()<=largo) { // La linea es mas corta que el largo admitido.
                texto="";
            } else { // Es de igual tamanio o mas larga.
                ultimoEspacio=texto.lastIndexOf(" ", largo);
                if (ultimoEspacio<0) { // Una palabra demasiado larga - la cortamos.
                    texto=texto.substring(largo, texto.length());
                } else { // Caso normal.
                    texto=texto.substring(ultimoEspacio+1);
                }
            }
            resultado = foo(alineacion, largo);
        }
        return resultado;
    }

    private static String foo(char alineacion, int largo) {
        String estaLinea = "";
        switch (alineacion) {
            case PARRAFO_ALINEDO_A_LA_DERECHA:
                return StringUtil.nullStringToEmpty("") + estaLinea + EOL;

            case PARRAFO_CENTRADO:
            case PARRAFO_CENTRADO_CON_RELLENO:
                int izquierda = (largo) / 2;
                return StringUtil.nullStringToEmpty("") + estaLinea
                        + (alineacion == PARRAFO_CENTRADO_CON_RELLENO ? StringUtil.nullStringToEmpty("") : "")
                        + EOL;

            case PARRAFO_JUSTIFICADO:
                return estaLinea + StringUtil.nullStringToEmpty("") + EOL;

            case PARRAFO_ALINEDO_A_LA_IZQUIERDA:
            case PARRAFO_ALINEDO_A_LA_IZQUIERDA_CON_RELLENO:
            default:
                return estaLinea
                        + (alineacion == PARRAFO_ALINEDO_A_LA_IZQUIERDA_CON_RELLENO ? StringUtil.nullStringToEmpty("") : "")
                        + EOL;
        }
    }
    
    public static String prepararFirmaCorreo(String firmaCorreo) {
    	   
    	if (firmaCorreo == null || firmaCorreo.trim().equals("")) {
    		   firmaCorreo = "";
        }
    	
    	if (firmaCorreo.length() > 0
           		&& firmaCorreo.substring(firmaCorreo.length()-1).equals(EOL)) {
        	   
    		firmaCorreo = firmaCorreo.substring(0, firmaCorreo.length() - 1);
    	}
       
       if (firmaCorreo.indexOf(EOLchar) == -1) {
    	   firmaCorreo = parrafo(firmaCorreo, ANCHO_DE_LINEA_EN_EMAIL, PARRAFO_ALINEDO_A_LA_IZQUIERDA);
       } else {
    	   firmaCorreo += EOL;
       }
       
       return firmaCorreo;
    }

    /**
     * Para dicho metodo, debió pasar previamente el metodo de verificación de no nulos.
     *
     * @param contenido the contenido
     * @param firma     the firma
     * @return the string
     */
    public static String prepararCuerpoMail(String contenido, String firma) {

        contenido = contenido == null ? "" : contenido;
        firma = firma == null ? "" : firma;

        return contenido.concat(firma.equals("")? "" : TextosUtil.EOL.concat("-- ").concat(TextosUtil.EOL).concat(firma));
    }
	
	
	

}
