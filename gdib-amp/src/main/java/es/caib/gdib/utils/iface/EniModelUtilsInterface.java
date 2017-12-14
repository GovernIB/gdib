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
public interface EniModelUtilsInterface {

	/**
	 * Caib namespace
	 */
    public static final String NS_ENI = "http://www.administracionelectronica.gob.es/model/eni/1.0";

    /**
     * Caib model
     *
     */
    public static final String ENI_PREFIX = "eni";
    public static final String ENI_ID = "ES_";

    public static final String ENI_MODEL_PREFIX = ENI_PREFIX + ConstantUtils.PREFIX_SEPARATOR;

    public static final String MODEL_ENI = "documentmodel";
    public static final QName MODEL_ENI_QNAME = QName.createQName(NS_ENI, MODEL_ENI);

    /**
     *  ************************ TYPES ************************
     */

    /**
	 * Type eni:expediente
	 */
    public static final String TYPE_EXPEDIENTE = "expediente";
    public static final QName TYPE_EXPEDIENTE_QNAME = QName.createQName(NS_ENI, TYPE_EXPEDIENTE);
    public static final String PROP_APP_TRAMITE_EXP = "app_tramite_exp";
    public static final QName PROP_APP_TRAMITE_EXP_QNAME = QName.createQName(NS_ENI, PROP_APP_TRAMITE_EXP);

    /**
     * Type eni:agregacionDoc
     */
    public static final String TYPE_AGREGACION_DOC = "agregacionDoc";
    public static final QName TYPE_AGREGACION_DOC_QNAME = QName.createQName(NS_ENI, TYPE_AGREGACION_DOC);

    /**
     * Type eni:docBase
     */
    public static final String TYPE_DOC_BASE = "docBase";
    public static final QName TYPE_DOC_BASE_QNAME = QName.createQName(NS_ENI, TYPE_DOC_BASE);
    public static final String PROP_APP_TRAMITE_DOC = "app_tramite_doc";
    public static final QName PROP_APP_TRAMITE_DOC_QNAME = QName.createQName(NS_ENI, PROP_APP_TRAMITE_DOC);

    /**
     * Type eni:documento
     */
    public static final String TYPE_DOCUMENTO = "documento";
    public static final QName TYPE_DOCUMENTO_QNAME = QName.createQName(NS_ENI, TYPE_DOCUMENTO);

    /**
     *  ************************ ASPECTS ************************
     */

    /**
     * Aspect eni:transferible
     */
    public static final String ASPECT_TRANSFERIBLE = "transferible";
    public static final QName ASPECT_TRANSFERIBLE_QNAME = QName.createQName(NS_ENI, ASPECT_TRANSFERIBLE);
    public static final String PROP_CATEGORIA = "categoria";
    public static final QName PROP_CATEGORIA_QNAME = QName.createQName(NS_ENI, PROP_CATEGORIA);
    public static final String PROP_LOPD = "lopd";
    public static final QName PROP_LOPD_QNAME = QName.createQName(NS_ENI, PROP_LOPD);
    public static final String PROP_CONFIDENCIALIDAD = "confidencialidad";
    public static final QName PROP_CONFIDENCIALIDAD_QNAME = QName.createQName(NS_ENI, PROP_CONFIDENCIALIDAD);
    public static final String PROP_TIPO_ACCESO = "tipo_acceso";
    public static final QName PROP_TIPO_ACCESO_QNAME = QName.createQName(NS_ENI, PROP_TIPO_ACCESO);
    public static final String PROP_CODIGO_CAUSA_LIMITACION = "codigo_causa_limitacion";
    public static final QName PROP_CODIGO_CAUSA_LIMITACION_QNAME = QName.createQName(NS_ENI,PROP_CODIGO_CAUSA_LIMITACION);
    public static final String PROP_FASE_ARCHIVO = "fase_archivo";
    public static final QName PROP_FASE_ARCHIVO_QNAME = QName.createQName(NS_ENI, PROP_FASE_ARCHIVO);
    public static final String PROP_FECHA_FIN_EXP = "fecha_fin_exp";
    public static final QName PROP_FECHA_FIN_EXP_QNAME = QName.createQName(NS_ENI, PROP_FECHA_FIN_EXP);
    public static final String PROP_ESTADO_ARCHIVO = "estado_archivo";
    public static final QName PROP_ESTADO_ARCHIVO_QNAME = QName.createQName(NS_ENI, PROP_ESTADO_ARCHIVO);
    public static final String PROP_NORMATIVA = "normativa";
    public static final QName PROP_NORMATIVA_QNAME = QName.createQName(NS_ENI, PROP_NORMATIVA);
    public static final String PROP_COND_REUTILIZACION = "cond_reutilizacion";
    public static final QName PROP_COND_REUTILIZACION_QNAME = QName.createQName(NS_ENI, PROP_COND_REUTILIZACION);
    public static final String PROP_TIPO_VALOR = "tipo_valor";
    public static final QName PROP_TIPO_VALOR_QNAME = QName.createQName(NS_ENI, PROP_TIPO_VALOR);
    public static final String PROP_PLAZO = "plazo";
    public static final QName PROP_PLAZO_QNAME = QName.createQName(NS_ENI, PROP_PLAZO);
    public static final String PROP_VALOR_SECUNDARIO = "valor_secundario";
    public static final QName PROP_VALOR_SECUNDARIO_QNAME = QName.createQName(NS_ENI, PROP_VALOR_SECUNDARIO);
    public static final String PROP_TIPO_DICTAMENT = "tipo_dictamen";
    public static final QName PROP_TIPO_DICTAMENT_QNAME = QName.createQName(NS_ENI, PROP_TIPO_DICTAMENT);
    public static final String PROP_ACCION_DICTAMINADA = "accion_dictaminada";
    public static final QName PROP_ACCION_DICTAMINADA_QNAME = QName.createQName(NS_ENI, PROP_ACCION_DICTAMINADA);
    public static final String PROP_PLAZO_ACCION_DICTAMINADA = "plazo_accion_dictaminada";
    public static final QName PROP_PLAZO_ACCION_DICTAMINADA_QNAME = QName.createQName(NS_ENI, PROP_PLAZO_ACCION_DICTAMINADA);
    public static final String PROP_DOCUMENTO_VITAL = "documento_vital";
    public static final QName PROP_DOCUMENTO_VITAL_QNAME = QName.createQName(NS_ENI, PROP_DOCUMENTO_VITAL);
    public static final String PROP_DENOMINACION_CLASE = "denominacion_clase";
    public static final QName PROP_DENOMINACION_CLASE_QNAME = QName.createQName(NS_ENI, PROP_DENOMINACION_CLASE);
    public static final String PROP_TIPO_CLASIFICACION = "tipo_clasificacion";
    public static final QName PROP_TIPO_CLASIFICACION_QNAME = QName.createQName(NS_ENI, PROP_TIPO_CLASIFICACION);

    /**
     * Aspect eni:firmadoBase
     */
    public static final String ASPECT_FIRMADO_BASE = "firmadoBase";
    public static final QName ASPECT_FIRMADO_BASE_QNAME = QName.createQName(NS_ENI, ASPECT_FIRMADO_BASE);
    public static final String PROP_PERFIL_FIRMA = "perfil_firma";
    public static final QName PROP_PERFIL_FIRMA_QNAME = QName.createQName(NS_ENI, PROP_PERFIL_FIRMA);
    public static final String PROP_FECHA_SELLADO = "fecha_sellado";
    public static final QName PROP_FECHA_SELLADO_QNAME = QName.createQName(NS_ENI, PROP_FECHA_SELLADO);
    public static final String PROP_CSV = "csv";
    public static final QName PROP_CSV_QNAME = QName.createQName(NS_ENI, PROP_CSV);
    public static final String PROP_TIPO_FIRMA = "tipoFirma";
    public static final QName PROP_TIPO_FIRMA_QNAME = QName.createQName(NS_ENI, PROP_TIPO_FIRMA);

    /**
     * Aspect eni:firmado
     */
    public static final String ASPECT_FIRMADO = "firmado";
    public static final QName ASPECT_FIRMADO_QNAME = QName.createQName(NS_ENI, ASPECT_FIRMADO);
    public static final String PROP_FIRMA = "firma";
    public static final QName PROP_FIRMA_QNAME = QName.createQName(NS_ENI, PROP_FIRMA);

    /**
     * Aspect eni:interoperable
     */
    public static final String ASPECT_INTEROPERABLE = "interoperable";
    public static final QName ASPECT_INTEROPERABLE_QNAME = QName.createQName(NS_ENI, ASPECT_INTEROPERABLE);
    public static final String PROP_ID = "id";
    public static final QName PROP_ID_QNAME = QName.createQName(NS_ENI, PROP_ID);
    public static final String PROP_ORGANO = "organo";
    public static final QName PROP_ORGANO_QNAME = QName.createQName(NS_ENI, PROP_ORGANO);
    public static final String PROP_V_NTI = "v_nti";
    public static final QName PROP_V_NTI_QNAME = QName.createQName(NS_ENI, PROP_V_NTI);
    public static final String PROP_ORIGEN = "origen";
    public static final QName PROP_ORIGEN_QNAME = QName.createQName(NS_ENI, PROP_ORIGEN);
    public static final String PROP_ID_ORIGEN = "id_origen";
    public static final QName PROP_ID_ORIGEN_QNAME = QName.createQName(NS_ENI, PROP_ID_ORIGEN);
    public static final String PROP_ESTADO_ELABORACION = "estado_elaboracion";
    public static final QName PROP_ESTADO_ELABORACION_QNAME = QName.createQName(NS_ENI, PROP_ESTADO_ELABORACION);
    public static final String PROP_TIPO_DOC_ENI = "tipo_doc_ENI";
    public static final QName PROP_TIPO_DOC_ENI_QNAME = QName.createQName(NS_ENI, PROP_TIPO_DOC_ENI);
    public static final String PROP_FECHA_INICIO = "fecha_inicio";
    public static final QName PROP_FECHA_INICIO_QNAME = QName.createQName(NS_ENI, PROP_FECHA_INICIO);
    public static final String PROP_COD_CLASIFICACION = "cod_clasificacion";
    public static final QName PROP_COD_CLASIFICACION_QNAME = QName.createQName(NS_ENI, PROP_COD_CLASIFICACION);
    public static final String PROP_NOMBRE_FORMATO = "nombre_formato";
    public static final QName PROP_NOMBRE_FORMATO_QNAME = QName.createQName(NS_ENI, PROP_NOMBRE_FORMATO);
    public static final String PROP_EXTENSION_FORMATO = "extension_formato";
    public static final QName PROP_EXTENSION_FORMATO_QNAME = QName.createQName(NS_ENI, PROP_EXTENSION_FORMATO);
    public static final String PROP_DEF_CSV = "def_csv";
    public static final QName PROP_DEF_CSV_QNAME = QName.createQName(NS_ENI, PROP_DEF_CSV);
    public static final String PROP_RESOLUCION = "resolucion";
    public static final QName PROP_RESOLUCION_QNAME = QName.createQName(NS_ENI, PROP_RESOLUCION);
    public static final String PROP_IDIOMA = "idioma";
    public static final QName PROP_IDIOMA_QNAME = QName.createQName(NS_ENI, PROP_IDIOMA);
    public static final String PROP_ESTADO_EXP = "estado_exp";
    public static final QName PROP_ESTADO_EXP_QNAME = QName.createQName(NS_ENI, PROP_ESTADO_EXP);
    public static final String PROP_INTERESADOS_EXP = "interesados_exp";
    public static final QName PROP_INTERESADOS_EXP_QNAME = QName.createQName(NS_ENI, PROP_INTERESADOS_EXP);
    public static final String PROP_TAMANO_LOGICO_EXP = "tamano_logico";
    public static final QName PROP_TAMANO_LOGICO_EXP_QNAME = QName.createQName(NS_ENI, PROP_TAMANO_LOGICO_EXP);
    public static final String PROP_DESCRIPCION_EXP = "descripcion";
    public static final QName PROP_DESCRIPCION_EXP_QNAME = QName.createQName(NS_ENI, PROP_DESCRIPCION_EXP);
    public static final String PROP_SOPORTE = "soporte";
    public static final QName PROP_SOPORTE_QNAME = QName.createQName(NS_ENI, PROP_SOPORTE);
    public static final String PROP_LOC_ARCHIVO_CENTRAL = "loc_archivo_central";
    public static final QName PROP_LOC_ARCHIVO_CENTRAL_QNAME = QName.createQName(NS_ENI, PROP_LOC_ARCHIVO_CENTRAL);
    public static final String PROP_LOC_ARCHIVO_GENERAL = "loc_archivo_general";
    public static final QName PROP_LOC_ARCHIVO_GENERAL_QNAME = QName.createQName(NS_ENI, PROP_LOC_ARCHIVO_GENERAL);
    public static final String PROP_UNIDADES = "unidades";
    public static final QName PROP_UNIDADES_QNAME = QName.createQName(NS_ENI, PROP_UNIDADES);
    public static final String PROP_SUBTIPO_DOC = "subtipo_doc";
    public static final QName PROP_SUBTIPO_DOC_QNAME = QName.createQName(NS_ENI, PROP_SUBTIPO_DOC);
    public static final String PROP_ID_TRAMITE = "id_tramite";
    public static final QName PROP_ID_TRAMITE_QNAME = QName.createQName(NS_ENI, PROP_ID_TRAMITE);
    public static final String PROP_PROFUNDIDAD_COLOR = "profundidad_color";
    public static final QName PROP_PROFUNDIDAD_COLOR_QNAME = QName.createQName(NS_ENI, PROP_PROFUNDIDAD_COLOR);
    public static final String PROP_TERMINO_PUNTO_ACCESO = "termino_punto_acceso";
    public static final QName PROP_TERMINO_PUNTO_ACCESO_QNAME = QName.createQName(NS_ENI, PROP_TERMINO_PUNTO_ACCESO);
    public static final String PROP_ID_PUNTO_ACCESO = "id_punto_acceso";
    public static final QName PROP_ID_PUNTO_ACCESO_QNAME = QName.createQName(NS_ENI, PROP_ID_PUNTO_ACCESO);
    public static final String PROP_ESQUEMA_PUNTO_ACCESO = "esquema_punto_acceso";
    public static final QName PROP_ESQUEMA_PUNTO_ACCESO_QNAME = QName.createQName(NS_ENI, PROP_ESQUEMA_PUNTO_ACCESO);

    /**
     * Aspect eni:registrable
     */
    public static final String ASPECT_REGISTABLE = "registrable";
    public static final QName ASPECT_REGISTABLE_QNAME = QName.createQName(NS_ENI, ASPECT_REGISTABLE);
    public static final String PROP_TIPO_ASIENTO_REGISTRAL = "tipo_asiento_registral";
    public static final QName PROP_TIPO_ASIENTO_REGISTRAL_QNAME = QName.createQName(NS_ENI, PROP_TIPO_ASIENTO_REGISTRAL);
    public static final String PROP_CODIGO_OFICINA_REGISTRO = "codigo_oficina_registro";
    public static final QName PROP_CODIGO_OFICINA_REGISTRO_QNAME = QName.createQName(NS_ENI, PROP_CODIGO_OFICINA_REGISTRO);
    public static final String PROP_FECHA_ASIENTO_REGISTRAL = "fecha_asiento_registral";
    public static final QName PROP_FECHA_ASIENTO_REGISTRAL_QNAME = QName.createQName(NS_ENI, PROP_FECHA_ASIENTO_REGISTRAL);
    public static final String PROP_NUMERO_ASIENTO_REGISTRAL = "numero_asiento_registral";
    public static final QName PROP_NUMERO_ASIENTO_REGISTRAL_QNAME = QName.createQName(NS_ENI, PROP_NUMERO_ASIENTO_REGISTRAL);

    /**
     * Aspect eni:marca_expurgo
     */
    public static final String ASPECT_MARCA_HISTORICO = "marca_expurgo";
    public static final QName ASPECT_MARCA_HISTORICO_QNAME = QName.createQName(NS_ENI, ASPECT_MARCA_HISTORICO);
    public static final String PROP_FECHA_MARCA_EXPURGO = "fecha_marca_expurgo";
    public static final QName PROP_FECHA_MARCA_EXPURGO_QNAME = QName.createQName(NS_ENI, PROP_FECHA_MARCA_EXPURGO);

}
