package es.caib.gdib.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import es.caib.gdib.utils.iface.CaibConstraintsUtilsInterface;
import es.caib.gdib.utils.iface.CaibExceptionsUtilsInterface;
import es.caib.gdib.utils.iface.EniModelUtilsInterface;
import es.caib.gdib.utils.iface.CaibModelUtilsInterface;

public class ConstantUtils implements CaibModelUtilsInterface, EniModelUtilsInterface, CaibConstraintsUtilsInterface, CaibExceptionsUtilsInterface {

	/* Constantes para clases */
	public final static String USER_ADMIN = "admin";

	/* Constantes de utils */
	public final static String NAMESPACE_BEGIN = "" + QName.NAMESPACE_BEGIN;
	public final static String NAMESPACE_END = "" + QName.NAMESPACE_END;
	public static final Pattern PATH_PATTERN = Pattern.compile("\\s*((["+Pattern.quote("\\")+"]+)|([/]+))+\\s*");
	public static final Pattern START_PATH_PATTERN = Pattern.compile("^\\s*(["+Pattern.quote("\\")+ "]|[/])+\\s*");
	public static final Pattern END_PATH_PATTERN = Pattern.compile("\\s*(["+Pattern.quote("\\")+ "]|[/])+\\s*$");
	public static final String SITE_FOLDER_NAME = "Sitios";
	public static final String SITES_PATH = "st:sites";
	public static final String ALFRESCO_CONTENT_MODEL_PREFIX = "cm";
	public static final Pattern ASPECT_PATTERN = Pattern.compile("^P:"+Pattern.quote("{")+".*"+Pattern.quote("}")+".*:");
	public static final String SPACESSTORE_PREFIX = StoreRef.PROTOCOL_WORKSPACE + StoreRef.URI_FILLER + "SpacesStore/";
	public static final String SPACESSTORE_PROTOCOL = StoreRef.PROTOCOL_WORKSPACE;
	public static final String ROOT = "app:company_home";
	public static final String PATH = "PATH";
	public static final String PATH_SEPARATOR = "/";
	public static final String CSV_SEPARATOR = ";";
	public static final String COMMA_SEPARATOR = ",";

	public static final String PERMISSION_READ = "read";
	public static final String PERMISSION_WRITE = "write";
	public static final List<String> PERMISSIONS = Arrays.asList(
			PERMISSION_READ,
			PERMISSION_WRITE);

	public static final String REPO_MIGR = "Migracion";

	/**
	 * Utilizada para saber si las propiedades van a ir en formato prefijo o cadena Qname
	 */
	public static final String GDIB_REPOSITORY_QNAME_PREFIX = "prefix";
	public static final String GDIB_MODIFY_PROPERTIES_ESB_OPERATION = "metadataCollection";

	/*** PREFIX separator */
	public static final String PREFIX_SEPARATOR = String.valueOf(QName.NAMESPACE_PREFIX);

	/*** PROPIEDADES del modelo **/
	public static final String NAME="{http://www.alfresco.org/model/content/1.0}name";
	public static final QName PROP_NAME = QName.createQName(NAME);
	public static final String FOLDER="{http://www.alfresco.org/model/content/1.0}folder";
	public static final QName TYPE_FOLDER = QName.createQName(FOLDER);
	public static final String CONTENT="{http://www.alfresco.org/model/content/1.0}content";
	public static final QName TYPE_CONTENT = QName.createQName(CONTENT);
	public static final QName PROP_CONTENT = ContentModel.PROP_CONTENT;

	public static final String REMOVE_PROPERTY_TOKEN = "-";

	public static final CharSequence NS_SYSTEM_MODEL = NamespaceService.SYSTEM_MODEL_1_0_URI;

	/**
	 * Identificadores para las propiedades Calculadas
	 */

	public static final String CALCULATED_URI = "http://www.ricoh.es/model/gdib/calculated/1.0";
    public static final String CALCULATED_PREFIX = "calc";
    public static final String CALCULATED_MODEL_PREFIX = CALCULATED_PREFIX + PREFIX_SEPARATOR;
    public static final String CALCULATED_MIME_TYPE = "mimeType";
    public static final String CALCULATED_PATH = "path";
    public static final String CALCULATED_SITE = "site";
    public static final String CALCULATED_PARENT = "primaryParent";

    /**
     * La version son dos numeros separados por .
     * 		Ej.: 12.1  -  1.20
     */
    public static final String exreg_VERSION = "\\d+\\.\\d+";

	/*** Expresiones regulares para la validacion NodeId **/
	/**
	 * Los UUID en alfresco estan compuestos de 5 grupos (8-4-4-4-12) de carateres separados por '-'
	 * que pueden ser numeros de 0 a 9 y letras minusculas de a hasta f incluidas.
	 *
	 * 		Ej.: b24eeb92-aed8-439c-af4d-db25785b2fc4
	 */
	public static final String exreg_UUID = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";
	public static final int UUID_LENGTH = 36;
	public static final int UUID_LENGTH_WITHOUT_DASH = 32;

    /**
	 * Las rutas en alfresco deben cumplir 3 requisitos:
	 * 		i/ No contener ningun caracter del grupo: [^"*\><?:|]
	 * 		ii/ No terminar en uno o mas caracter '.'
	 * 		iii/ No terminar en uno o mas caracter ' '.
	 *
	 * 		Ej.: /Sanidad/EXP001234/2016/04/01/AJP-20282782734
	 */
	public static final String exreg_ALFRUTA = ".[^\\\"\\*\\\\\\>\\<\\?\\:\\|]*(?<![\\.\\s])";

    /**
     * Expresion regular para cadenas que identifican un Nodo en forma de UUID de Alfresco
     * 		Ej.: b24eeb92-aed8-439c-af4d-db25785b2fc4
     *
     * GRUPO 1: Noderef
     */
    public static final Pattern UUID_PATTERN = Pattern.compile("^(" + exreg_UUID + ")$");

    /**
     * Expresion regular para cadenas que identifican un Nodo en forma de version@UUID de Alfresco
     * 		Ej.: 1.1@b24eeb92-aed8-439c-af4d-db25785b2fc4
     *
     * GRUPO 1: Version
     * GRUPO 2: Noderef
     */
    public static final Pattern VERSION_UUID_PATTERN = Pattern.compile("^(" + exreg_VERSION + ")@(" + exreg_UUID + ")$");

    /**
     * Expresion regular para cadenas que identifican un Nodo en forma de ruta absoluta en el repositorio
     * 		Ej.: /Sanidad/EXP001234/2016/04/01/AJP-20282782734
     *
     * GRUPO 1: Ruta absoluta
     */
    public static final Pattern PATH_ABS_PATTERN = Pattern.compile("^(/" + exreg_ALFRUTA + ")$");

    /**
     * Expresion regular para cadenas que identifican un Nodo en forma de UUID mas su ruta relativa
     * 		Ej.:	b24eeb92-aed8-439c-af4d-db25785b2fc4/ruta/al/nodo
     *
     * GRUPO 1: Noderef
     * GRUPO 2: Ruta relativa
     */
    public static final Pattern PATH_REL_PATTERN = Pattern.compile("^(" + exreg_UUID + ")(/" + exreg_ALFRUTA + ")$");

    /**
     * Expresion regular para cadenas que identifican la version de alfresco
     * 		Ej.: 1.1
     *
     * GRUPO1: Version
     */
    public static final Pattern VERSION_PATTERN = Pattern.compile("^(" + exreg_VERSION + ")$");

    public static final String EXPEDIENT_EXPORT_FOLDER_NAME = "export";

    public static final String BLANK = "";
    public static final String BLANK_TEXT = "blank value";

    public static final String exreg_GENERATE_UUID = "([\\da-f]{8})([\\da-f]{4})([\\da-f]{4})([\\da-f]{4})([\\da-f]{12})";
    public static final Pattern GENERATE_UUID_PATTERN = Pattern.compile("^" + exreg_GENERATE_UUID + "$");

    public static final String V_NTI_EXP = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e";
    public static final String V_NTI_DOC = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/documento-e";

    public static final String FIRMA_VALCERT = ".firma";
    public static final String FIRMA_MIGRACION = ".firmaMigracion";
    public static final String FIRMA_MIGRACION_ZIP = ".zipMigracion.zip";

    public static final String INTERNAL_INDEX_NAME_PREFIX = "indice-";

    public static final String EXCHANGE_INDEX_NAME_PREFIX = "indice-int-";

    public static final String DEF_CSV_VALUE = "def_csv_value";

    public static final String DEFAULT_SUBTYPE_DOC_VALUE = "@defecto";

    public static final QName PROP_NODE_UUID = ContentModel.PROP_NODE_UUID;

    public static final String ESB_OPERATION_REGISTRY = "dispatchDocument";

    //Generación índice electrónico expediente
    public static final Boolean DEFAULT_ADD_EXCHANGE_FILES_VALUE = Boolean.FALSE;

    public static final String INDEX_ID_ATT_KEY = "INDICE_ID";

	public static final String INDEX_ID_ATT_VALUE = "INDICE_ID_1";

	public static final String IND_ID_ATT_PREFIX = "IND_";

	public static final String DOC_ID_ATT_PREFIX = "DOC_";

	public static final String FOLDER_ID_ATT_PREFIX = "CPT_";

	public static final String FILE_ID_ATT_PREFIX = "EXP_";

	public static final String INDEX_FILE_CONTENT_ID_ATT_PREFIX = "EXP_CONT_";

	public static final String METADATA_ID_ATT_PREFIX = "MTD_";

	public static final String SIGNATURE_ID_ATT_PREFIX = "FIR_";
}
