package es.caib.arxiudigital.apirest.constantes;

/**
 * 
 * 
 * @author u104848
 *
 */
public abstract class MetadatosDocumento {

	//
	// Datos obligatorios
	//
	/**
	 * Código de la aplicación de trámite que generó el expediente.
	 */
	public final static String CODIGO_APLICACION_TRAMITE	= "eni:app_tramite_doc";
	public final static String ORGANO	= "eni:organo";
	public final static String ORIGEN	= "eni:origen";
	/**
	 * Estado de la situación de elaboración del documento.
	 */
	public final static String ESTADO_ELABORACION  		= "eni:estado_elaboracion";
	/**
	 * Tipo de documento ENI.
	 */
	public final static String TIPO_DOC_ENI  			= "eni:tipo_doc_ENI";
	/**
	 * Identificador único codificado que determina una categoría en el Cuadro de Clasificación de CAIB.
	 */
	public final static String CODIGO_CLASIFICACION		= "eni:cod_clasificacion";

	public final static String CSV	= "eni:csv";
	/**
	 * Referencia a la disposición normativa que define la creación y uso del CSV correspondiente.
	 * Metadato requerido
	 */
	public final static String DEF_CSV = "eni:def_csv";
	
	public final static String ID_ENI	= "eni:id";
	public final static String ID_ORIGEN	= "eni:id_origen";

	/** 
	 * Fecha de captura del documento o apertura del expediente en el sistema (Optativo). 
	 * */
	public final static String FECHA_INICIO	  	  			= "eni:fecha_inicio";
	public final static String NOMBRE_FORMATO	  	  		= "eni:nombre_formato";
	public final static String EXTENSION_FORMATO	  		= "eni:extension_formato";
	public final static String RESOLUCION	  	  			= "eni:fecha_inicio";
	public final static String IDIOMA	  		  			= "eni:idioma";
	public final static String TAMAÑO_LOGICO  	  			= "eni:tamano_logico";
	public final static String PROFUNDIDAD_COLOR  			= "eni:profundidad_color";
	public final static String DESCRIPCION_DOCUMENTO  		= "eni:descripcion";

	public final static String TERMINO_PTO_ACCESO  			= "eni:termino_punto_acceso";
	public final static String ID_PTO_ACCESO	  		  	= "eni:id_punto_acceso";
	public final static String ESQUEMA_PTO_ACCESO  			= "eni:esquema_punto_acceso";
	/**
	 * Si no es informada por el sistema de información, se establece el valor "Digital".
	 */
	public final static String SOPORTE	  		  			= "eni:soporte";
	public final static String LOCALIZACION_ARCH_CENTRAL	= "eni:loc_archivo_central";
	public final static String LOCALIZACION_ARCH_GENERAL	= "eni:loc_archivo_general";
	public final static String UNIDADES	  		  			= "eni:unidades";
	/**
	 * En caso de ser informado, debe validarse contra el cuadro de clasificación definido en el módulo RM. Esta subclasificación debe ser hija 
	 * de la serie definida para el documento en el metadato "eni:cod_clasificacion".
	 */
	public final static String SUBTIPO	  		  			= "eni:subtipo_doc";
	public final static String TIPO_ASIENTO_REGISTRAL		= "eni:tipo_asiento_registral";
	public final static String CODIGO_OFICINA_REGISTRO		= "eni:codigo_oficina_registro";
	public final static String FECHA_ASIENTO_REGISTRAL	  	= "eni:fecha_asiento_registral";
	public final static String NUMERO_ASIENTO_REGISTRAL	 	= "eni:numero_asiento_registral";
	/**
	 * Requerido si se efectua traslado de documentos desde/hacia Registro General.
	 */
	public final static String FECHA_TRASLADO	  			= "gdib:fecha_traslado";
	/**
	 * Requerido si se efectua traslado de documentos desde/hacia Registro General.
	 */
	public final static String AUTOR_TRASLADO	  			= "gdib:autor_traslado";
	/**
	 * Requerido si se efectua traslado de documentos desde/hacia Registro General.
	 */
	public final static String DESTINO_TRASLADO	  			= "gdib:destino_traslado";
	/**
	 * Requerido si se efectua traslado de documentos desde/hacia Registro General.
	 */
	public final static String ID_NODO_NUEVA_LOCALIZACION	= "gdib:id_nodo_nueva_loc";
	/**
	 * Requerido si se efectua traslado de documentos desde/hacia Registro General.
	 */
	public final static String TIPO_DESTINO	  		  		= "gdib:tipo_destino";

	/**
	 * Denominación normalizada del tipo de firma.
	 */
	public final static String TIPO_FIRMA	  		  		= "eni:tipoFirma";

	/**
	 * Perfil empleado en una firma con certificado electrónico.
	 */
	public final static String PERFIL_FIRMA	  		  		= "eni:perfil_firma";

	/**
	 * Fecha en la que fue resellado el documento por última vez. Un valor nulo, indica que nunca ha sido resellado.
	 */
	public final static String FECHA_SELLADO	  		  	= "eni:fecha_sellado";


}
