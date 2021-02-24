package es.caib.gdib.utils.iface;

import org.alfresco.service.namespace.QName;

import es.caib.gdib.utils.ConstantUtils;

/**
 * Interfaz que contiene todas las constantes relacionadas con el Modelo
 * <ul>
 * 	<li>Nombres</li>
 * 	<li>Qnames</li>
 * 	<li>Prefijo del modelo</li>
 * 	<li>Namespace del modelo</li>
 * </ul>
 *
 * @author RICOH
 *
 */
public interface CaibModelUtilsInterface {

	/**
	 * Caib namespace
	 */
    public static final String NS_CAIB = "http://www.caib.es/model/gdib/1.0";

    /**
     * Caib model
     *
     */
    public static final String CAIB_PREFIX = "gdib";

    public static final String CAIB_MODEL_PREFIX = CAIB_PREFIX + ConstantUtils.PREFIX_SEPARATOR;

    /**
     *  ************************ TYPES ************************
     */
    /**
     * Type gdib:cuadro_clasificacion
     */
    public static final String TYPE_CUADRO_CLASIFICACION = "cuadro_clasificacion";
    public static final QName TYPE_CUADRO_CLASIFICACION_QNAME = QName.createQName(NS_CAIB,TYPE_CUADRO_CLASIFICACION);
    public static final String PROP_CODIGO_CUADRO = "codigo_cuadro";
    public static final QName PROP_CODIGO_CUADRO_QNAME = QName.createQName(NS_CAIB,PROP_CODIGO_CUADRO);
    public static final String PROP_ESTADO_CUADRO = "estado";
    public static final QName PROP_ESTADO_CUADRO_QNAME = QName.createQName(NS_CAIB,PROP_ESTADO_CUADRO);

    /**
     * Type gdib:funcion
     */
    public static final String TYPE_FUNCION = "funcion";
    public static final QName TYPE_FUNCION_QNAME = QName.createQName(NS_CAIB,TYPE_FUNCION);
    public static final String PROP_CODIGO_FUNCION = "codigo_funcion";
    public static final QName PROP_CODIGO_FUNCION_QNAME = QName.createQName(NS_CAIB,PROP_CODIGO_FUNCION);
    public static final String PROP_ESTADO_FUNCION = "estado";
    public static final QName PROP_ESTADO_FUNCION_QNAME = QName.createQName(NS_CAIB,PROP_ESTADO_FUNCION);

    /**
     * Type gdib:funcion_rm
     */
    public static final String ASPECT_FUNCION_RM = "funcion_rm_aspect";
    public static final QName ASPECT_FUNCION_RM_QNAME = QName.createQName(NS_CAIB,ASPECT_FUNCION_RM);

    /**
     * Type gdib:cuadro_rm
     */
    public static final String ASPECT_CUADRO_CLASIFICACION_RM = "cuadro_clasificacion_rm_aspect";
    public static final QName ASPECT_CUADRO_CLASIFICACION_RM_QNAME = QName.createQName(NS_CAIB,ASPECT_CUADRO_CLASIFICACION_RM);

    
    
    
    /**
     * Type gdib:serie
   
    public static final String TYPE_SERIE_DOCUMENTAL = "serie";
    public static final QName TYPE_SERIE_DOCUMENTAL_QNAME = QName.createQName(NS_CAIB,TYPE_SERIE_DOCUMENTAL);
    public static final String PROP_CODIGO_CLASIFICACION_SERIE = "codigo_clasificacion";
    public static final QName PROP_CODIGO_CLASIFICACION_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_CODIGO_CLASIFICACION_SERIE);
    public static final String PROP_LOPD_SERIE = "lopd";
    public static final QName PROP_LOPD_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_LOPD_SERIE);
    public static final String PROP_CONFIDENCIALIDAD_SERIE = "confidencialidad";
    public static final QName PROP_CONFIDENCIALIDAD_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_CONFIDENCIALIDAD_SERIE);
    public static final String PROP_TIPO_ACCESO_SERIE = "tipo_acceso";
    public static final QName PROP_TIPO_ACCESO_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_TIPO_ACCESO_SERIE);
    public static final String PROP_CAUSA_LIMITACION_SERIE = "causa_limitacion";
    public static final QName PROP_CAUSA_LIMITACION_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_CAUSA_LIMITACION_SERIE);
    public static final String PROP_NORMATIVA_SERIE = "normativa";

    public static final QName PROP_NORMATIVA_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_NORMATIVA_SERIE);
    public static final String PROP_CONDICION_REUTILIZACION_SERIE = "condicion_reutilizacion";
    public static final QName PROP_CONDICION_REUTILIZACION_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_CONDICION_REUTILIZACION_SERIE);
    public static final String PROP_TIPO_VALOR_SERIE = "tipo_valor";
    public static final QName PROP_TIPO_VALOR_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_TIPO_VALOR_SERIE);
    public static final String PROP_TERMINO_SERIE = "termino";
    public static final QName PROP_TERMINO_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_TERMINO_SERIE);
    public static final String PROP_VALOR_SECUNDARIO_SERIE ="valor_secundario";
    public static final QName PROP_VALOR_SECUNDARIO_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_VALOR_SECUNDARIO_SERIE);
    public static final String PROP_TIPO_DICTAMEN_SERIE = "tipo_dictamen";
    public static final QName PROP_TIPO_DICTAMEN_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_TIPO_DICTAMEN_SERIE);
    public static final String PROP_ACCION_DICTAMINADA_SERIE = "accion_dictaminada";
    public static final QName PROP_ACCION_DICTAMINADA_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_ACCION_DICTAMINADA_SERIE);
    public static final String PROP_TERMINO_ACCION_DICTAMINADA_SERIE = "termino_accion_dictaminada";
    public static final QName PROP_TERMINO_ACCION_DICTAMINADA_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_TERMINO_ACCION_DICTAMINADA_SERIE);
    public static final String PROP_SERIE_ESENCIAL_SERIE = "serie_esencial";
    public static final QName PROP_SERIE_ESENCIAL_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_SERIE_ESENCIAL_SERIE);
    public static final String PROP_TIPO_CLASIFICACION_SERIE = "tipo_clasificacion";
    public static final QName PROP_TIPO_CLASIFICACION_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_TIPO_CLASIFICACION_SERIE);
    public static final String PROP_RESELLADO_SERIE = "resellado";
    public static final QName PROP_RESELLADO_SERIE_QNAME = QName.createQName(NS_CAIB,PROP_RESELLADO_SERIE);
  */


    /**
     * Type gdib:documentoMigrado
     */
    public static final String TYPE_DOCUMENTO_MIGRADO = "documentoMigrado";
    public static final QName TYPE_DOCUMENTO_MIGRADO_QNAME = QName.createQName(NS_CAIB, TYPE_DOCUMENTO_MIGRADO);
    public static final String PROP_FECHA_MIGRACION = "fecha_migracion";
    public static final QName PROP_FECHA_MIGRACION_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_MIGRACION);
    public static final String PROP_CODIGO = "codigo";
    public static final QName PROP_CODIGO_QNAME = QName.createQName(NS_CAIB, PROP_CODIGO);
    public static final String PROP_FECHA_CUSTODIA = "fecha_custodia";
    public static final QName PROP_FECHA_CUSTODIA_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_CUSTODIA);
    public static final String PROP_VIGENTE = "vigente";
    public static final QName PROP_VIGENTE_QNAME = QName.createQName(NS_CAIB, PROP_VIGENTE);
    public static final String PROP_FECHA_FIN_VIGENCIA = "fecha_fin_vigencia";
    public static final QName PROP_FECHA_FIN_VIGENCIA_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_FIN_VIGENCIA);
    public static final String PROP_TIPO_DOCUMENTAL = "tipo_documental";
    public static final QName PROP_TIPO_DOCUMENTAL_QNAME = QName.createQName(NS_CAIB, PROP_TIPO_DOCUMENTAL);
    public static final String PROP_CODIGO_EXTERNO = "codigo_externo";
    public static final QName PROP_CODIGO_EXTERNO_QNAME = QName.createQName(NS_CAIB, PROP_CODIGO_EXTERNO);
    public static final String PROP_FECHA_CREACION = "fecha_creacion";
    public static final QName PROP_FECHA_CREACION_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_CREACION);
    public static final String PROP_FECHA_ELIMINACION = "fecha_eliminacion";
    public static final QName PROP_FECHA_ELIMINACION_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_ELIMINACION);
    public static final String PROP_FECHA_PURGADO = "fecha_purgado";
    public static final QName PROP_FECHA_PURGADO_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_PURGADO);
    public static final String PROP_CLASE = "clase";
    public static final QName PROP_CLASE_QNAME = QName.createQName(NS_CAIB, PROP_CLASE);
    public static final String PROP_HASH = "hash";
    public static final QName PROP_HASH_QNAME = QName.createQName(NS_CAIB, PROP_HASH);

    /**
     * Type gdib:firma
     */
    public static final String TYPE_FIRMA = "firma";
    public static final QName TYPE_FIRMA_QNAME = QName.createQName(NS_CAIB, TYPE_FIRMA);

    /**
     * Type gdib:firmaValCert
     */
    public static final String TYPE_FIRMA_VALCERT = "firmaValCert";
    public static final QName TYPE_FIRMA_VALCERT_QNAME = QName.createQName(NS_CAIB, TYPE_FIRMA_VALCERT);

    /**
     * Type gdib:firmaMigracion
     */
    public static final String TYPE_FIRMA_MIGRACION = "firmaMigracion";
    public static final QName TYPE_FIRMA_MIGRACION_QNAME = QName.createQName(NS_CAIB, TYPE_FIRMA_MIGRACION);

    /**
     * Type gdib:indiceExpediente
     */
    public static final String TYPE_FILE_INDEX = "indiceExpediente";
    public static final QName TYPE_FILE_INDEX_QNAME = QName.createQName(NS_CAIB, TYPE_FILE_INDEX);
    public static final String PROP_INDEX_TYPE = "tipo_indice";
    public static final QName PROP_INDEX_TYPE_QNAME = QName.createQName(NS_CAIB, PROP_INDEX_TYPE);
    /**
     * RESELLADO
     */
    //Indica si un indice es valido (ultimo resellado, ya que anteriores se configura esta propiedad a NO)
    public static final String PROP_INDEX_VALID = "indice_valido";
    public static final QName PROP_INDEX_VALID_QNAME = QName.createQName(NS_CAIB, PROP_INDEX_VALID);
    //Indicador de identificador del certificado firmante
    public static final String PROP_INDEX_CERT = "identificador_certificado";
    public static final QName PROP_INDEX_CERT_QNAME = QName.createQName(NS_CAIB, PROP_INDEX_CERT);
    //Indicador de fecha de validez del certificado
    public static final String PROP_INDEX_CERT_DATE = "fecha_validez_certificado";
    public static final QName PROP_INDEX_CERT_DATE_QNAME = QName.createQName(NS_CAIB, PROP_INDEX_CERT_DATE);
    
    
    /**
     * Type gdib:zipMigracion
     */
    public static final String TYPE_ZIP_MIGRACION = "zipMigracion";
    public static final QName TYPE_ZIP_MIGRACION_QNAME = QName.createQName(NS_CAIB, TYPE_ZIP_MIGRACION);

    /**
     *  ************************ ASPECTS ************************
     */
    /**
     * Aspect gdib:firmadoMigracion
     */
    public static final String ASPECT_FIRMADO_MIGRACION = "firmadoMigracion";
    public static final QName ASPECT_FIRMADO_MIGRACION_QNAME = QName.createQName(NS_CAIB, ASPECT_FIRMADO_MIGRACION);

    /**
     * Aspect "gdib:transformado"
     */
    public static final String ASPECT_TRANSFORMADO = "transformado";
    public static final QName ASPECT_TRANSFORMADO_QNAME = QName.createQName(NS_CAIB, ASPECT_TRANSFORMADO);
    public static final String PROP_FECHA_TRANSFORMACION = "fecha_transformacion";
    public static final QName PROP_FECHA_TRANSFORMACION_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_TRANSFORMACION);
    public static final String PROP_FECHA_MIGRACION_VALCERT = "fecha_migracion_valcert";
    public static final QName PROP_FECHA_MIGRACION_VALCERT_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_MIGRACION_VALCERT);
    public static final String PROP_CODIGO_VALCERT = "codigo_valcert";
    public static final QName PROP_CODIGO_VALCERT_QNAME = QName.createQName(NS_CAIB, PROP_CODIGO_VALCERT);
    public static final String PROP_TIPO_DOCUMENTAL_VALCERT = "tipo_documental_valcert";
    public static final QName PROP_TIPO_DOCUMENTAL_VALCERT_QNAME = QName.createQName(NS_CAIB, PROP_TIPO_DOCUMENTAL_VALCERT);
    public static final String PROP_CODIGO_EXTERNO_VALCERT = "codigo_externo_valcert";
    public static final QName PROP_CODIGO_EXTERNO_VALCERT_QNAME = QName.createQName(NS_CAIB, PROP_CODIGO_EXTERNO_VALCERT);
    public static final String PROP_CLASE_VALCERT = "clase_valcert";
    public static final QName PROP_CLASE_VALCERT_QNAME = QName.createQName(NS_CAIB, PROP_CLASE_VALCERT);
    public static final String PROP_FIRMAVALCERT = "firma_valcert";
    public static final QName PROP_FIRMAVALCERT_QNAME = QName.createQName(NS_CAIB, PROP_FIRMAVALCERT);
    public static final String PROP_ZIPMIGRACION = "zipMigracion";
    public static final QName PROP_ZIPMIGRACION_QNAME = QName.createQName(NS_CAIB, PROP_ZIPMIGRACION);
    public static final String PROP_TRANSFORM_UUID = "transform_uuid";
    public static final QName PROP_TRANSFORM_UUID_QNAME = QName.createQName(NS_CAIB, PROP_TRANSFORM_UUID);

    /**
     * Aspect gdib:borrador
     */
    public static final String ASPECT_BORRADOR = "borrador";
    public static final QName ASPECT_BORRADOR_QNAME = QName.createQName(NS_CAIB, ASPECT_BORRADOR);

    /**
     * Aspect gdib:trasladado
     */
    public static final String ASPECT_TRASLADADO = "trasladado";
    public static final QName ASPECT_TRASLADADO_QNAME = QName.createQName(NS_CAIB, ASPECT_TRASLADADO);
    public static final String PROP_FECHA_TRASLADO = "fecha_traslado";
    public static final QName PROP_FECHA_TRASLADO_QNAME = QName.createQName(NS_CAIB, PROP_FECHA_TRASLADO);
    public static final String PROP_AUTOR_TRASLADO = "autor_traslado";
    public static final QName PROP_AUTOR_TRASLADO_QNAME = QName.createQName(NS_CAIB, PROP_AUTOR_TRASLADO);
    public static final String PROP_DESTINO_TRASLADO = "destino_traslado";
    public static final QName PROP_DESTINO_TRASLADO_QNAME = QName.createQName(NS_CAIB, PROP_DESTINO_TRASLADO);
    public static final String PROP_ID_NODO_NUEVA_LOC = "id_nodo_nueva_loc";
    public static final QName PROP_ID_NODO_NUEVA_LOC_QNAME = QName.createQName(NS_CAIB, PROP_ID_NODO_NUEVA_LOC);
    public static final String PROP_TIPO_DESTINO = "tipo_destino";
    public static final QName PROP_TIPO_DESTINO_QNAME = QName.createQName(NS_CAIB, PROP_TIPO_DESTINO);

    /**
     * Aspect gdib:enlazado
     */
    public static final String ASPECT_ENLAZADO = "enlazado";
    public static final QName ASPECT_ENLAZADO_QNAME = QName.createQName(NS_CAIB, ASPECT_ENLAZADO);
    public static final String PROP_EXP_ENLAZADO = "exp_enlazado";
    public static final QName PROP_EXP_ENLAZADO_QNAME = QName.createQName(NS_CAIB, PROP_EXP_ENLAZADO);

    /**
     * Aspect gdib:historico
     */
    public static final String ASPECT_HISTORICO = "historico";
    public static final QName ASPECT_HISTORICO_QNAME = QName.createQName(NS_CAIB, ASPECT_HISTORICO);

    /**
     * Desarrollo reopen file
     */
    public static final String PROP_EXP_REAPERTURA ="exp_original";
    public static final QName PROP_EXP_REAPERTURA_QNAME= QName.createQName(NS_CAIB,PROP_EXP_REAPERTURA);
    public static final String PROP_EXP_REABIERTO = "exp_reabierto";
    public static final QName PROP_EXP_REABIERTO_QNAME= QName.createQName(NS_CAIB,PROP_EXP_REABIERTO);
    
}
