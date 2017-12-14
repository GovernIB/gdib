package es.caib.arxiudigital.apirest.constantes;

/**
 * 
 * 
 * @author u104848
 *
 */
public abstract class MetadatosExpediente {
	
	/**
	 * Código de la aplicación de trámite que generó el expediente.
	 */
	public final static String CODIGO_APLICACION_TRAMITE  	= "eni:app_tramite_exp";
	
	/**
	 * Identificador único codificado que determina una categoría en el Cuadro de Clasificación de CAIB.
	 */
	public final static String CODIGO_CLASIFICACION	  	  	= "eni:cod_clasificacion";
	
	/** 
	 * Fecha de captura del documento o apertura del expediente en el sistema (Optativo). 
	 * */
	public final static String FECHA_INICIO	  	  			= "eni:fecha_inicio";
	
	/**
	 * Identificador único del procedimiento administrativo con el que se relaciona el expediente.
	 * 
	 * Este identificador puede corresponderse, bien con el identificador del Sistema de Información Administrativa (SIA),
	 * bien con un identificador propio con el siguiente formato:
	 * <ORGANO>_PRO_<ID_PROC>
	 * 
	 */
	public final static String IDENTIFICADOR_PROCEDIMIENTO	= "eni:id_tramite";
	
	/**
	 * Estado del expediente en el momento de traslado (Abierto, Cerrado, Índice para remisión cerrado).
	 * 
	 */
	public final static String ESTADO_EXPEDIENTE          	= "eni:estado_exp";
	
	/**
	 * Identificador normalizado de la administración generadora o que captura el documento, o tramitadora del expediente (DIR3).
	 * 
	 */
	public final static String ORGANO					  	= "eni:organo";
	
	/**
	 * Origen del contenido: Ciudadano o administración
	 */
	public final static String ORIGEN  		              	= "eni:origen";
	
	/**
	 * Tipo de documento ENI.
	 */
	public final static String TIPO_DOCUMENTO_ENI          	= "eni:tipo_doc_ENI";
	
	/** 
	 * Fecha de captura del documento o apertura del expediente en el sistema 
	 * Aplica Expediente (Optativo). 
	 * 
	 * */
	public final static String INTERESADOS	  	  			= "eni:interesados_exp";
	
	/** 
	 * Información adicional sobre el documento o expediente. 
	 * Aplica Expediente/Documento (Optativo). 
	 * 
	 * */
	public final static String DESCRIPCION	  	  			= "eni:descripcion";
	
	
	/** 
	 * Palabra clave que describe el contenido del documento o de la regulación. 
	 * Aplica Documento (Optativo). 
	 * 
	 * */
	public final static String TERMINO_PUNTO_ACCESO			= "eni:termino_punto_acceso";
	
	/** 
	 * Identificador asignado a una palabra clave dentro de un esquema de puntos de acceso. 
	 * Aplica Documento (Optativo). 
	 * 
	 * */
	public final static String ID_PUNTO_ACCESO			= "eni:id_punto_acceso";
	
	
}
