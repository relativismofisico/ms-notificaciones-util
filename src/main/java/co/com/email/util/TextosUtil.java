package co.com.email.util;

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
     *
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
