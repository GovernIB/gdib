package es.caib.gdib.utils.iface;


public interface CaibExceptionsUtilsInterface {

	/* Codigos de error */
	//Errores generales 1xxx
	public static int CODE_GENERAL = 1000; // Codigo reservado para las excepciones generales de commons-utils
	public static int CODE_CSV = 1401;
	public static int CODE_GET_SIGN = 1402;
	public static int CODE_GET_MIGRATION_SIGN = 1403;
	public static int CODE_TRANSACTION_ERROR = 1999;

	//Check parametros 5xxx
	public static int CODE_CHECK_PARAMS	=5000;
	public static int CODE_NULL_PARAM 	=5001;
	public static int CODE_NOT_FOUND 	=5002;
	public static int CODE_CHECK_TYPE	=5003;
	public static int CODE_CHECK_PROPERTIES	=5004;
	public static int CODE_CHECK_PROPERTIES_VALUE	=5005;
	public static int CODE_CHECK_MANDATORY_PROPERTIES	=5006;
	public static int CODE_INFORM_PARAM	=5007;
	public static int CODE_INVALID_NODEID	=5008;
	public static int CODE_LOCKED_NODE	=5009;
	public static int CODE_INVALID_PERMISSSION	=5010;
	public static int CODE_INVALID_QNAME_FORMAT = 5011;
	public static int CODE_ALREADY_EXITS = 5012;
	public static int CODE_INVALID_PERMISSION = 5013;
	public static int CODE_CHECK_DOCUMENTARY_SERIES_IN_CLASSIFICATION_TABLE = 5014;
	public static int CODE_AUTHORIY_NOT_EXITS = 5016;
	public static int CODE_INVALID_MIGRATION_ID_VALUE = 5017;
	public static int CODE_NOT_FOUND_VERSION	=5020;
	public static int CODE_CHECK_NO_MODIFY_PROPERTIES = 5021;

	//Errores de lectura 61XX /escritura y modificacion 65XX
	public static int CODE_SIGN_WRITE 	=6100; //TODO cambiar
	public static int CODE_CONTENT_WRITE 	=6101;
	public static int CODE_GET_BYTE_FROM_DATAHANDLER = 6102;
	public static int CODE_NODE_INVALID_CONTENT = 6150;
	public static int CODE_INVALID_ASPECT = 6151;
	public static int CODE_CHANGE_TYPE_EXCEPTION = 6152;
	public static int CODE_INTEGRITY_ASPECT = 6153;
	public static int CODE_UPDATE_NODE_IS_A_VERSION = 6154;
	public static int CODE_FINALLY_NODE = 6155;
	public static int CODE_INVALID_ADD_DRAFT_ASPECT = 6156;
	public static int CODE_NOT_OPEN_EXPEDIENT = 6167;
	public static int CODE_VERSION_NOT_EXITS = 6168;
	public static int CODE_GENERATE_XML_GREGORIAN_CALENDAR = 6170;
	public static int CODE_NODE_NOT_IN_PATH = 6172;
	public static int CODE_CHECK_METADATA_INTEGRITY = 6173;
	public static int CODE_DOCUMENTARY_SERIES_NO_DOCUMENTED= 6180;
	public static int CODE_REOPEN_EXPURGATE_EXPEDIENT = 6190;

	//Errores con repositorio migracion 7XXX
	public static int CODE_MIGRATION_GENERAL = 7200;
	public static int CODE_MIGRATION_NODE = 7201;
	public static int CODE_MIGRATION_NODE_NOT_FOUND = 7202;
	public static int CODE_MIGRATED_NODE_DUPLICATED = 7203;

	//Errores con el servicio de foliado 8XXX
	public static int CODE_ERROR_INDICEELECTRONICO = 8000;
	public static int CODE_IS_A_EXCHANGE_EXPEDIENT = 8001;
	public static int CODE_FILE_IS_EMPTY = 8002;
	public static int CODE_INDEX_GEN_ERROR = 8003;

	//Errores con la configuracion de alfresco
	public static int CODE_CONFIGURATION_ROOT_RM = 10000;
	public static int CODE_CONFIGURATION_ROOT_TEMPLATE = 10001;

	/* Mensajes de error */
	public static final String MSG_CHECK_PARAMS="@param is not valid (@value)";
	public static final String MSG_NULL_PARAM="@param can't be null or empty";
	public static final String MSG_INFORM_PARAM="@param must be null";

	public static final String MSG_NODE_METADATA_ENI="@param is not valid (@value)";
	public static final String MSG_CHECK_TYPE = "@type not valid for node type";
	public static final String MSG_CHECK_PROPERTIES = "@property property not found in model";
	public static final String MSG_CHECK_PROPERTIES_VALUE = "@value is not valid for the property @property";
	public static final String MSG_CHECK_MANDATORY_PROPERTIES = "@property is required in the node";
	public static final String MSG_NODE_INVALID_CONTENT ="@nodeId has invalid content";
	public static final String MSG_INVALID_ASPECT = "@aspect is not valid aspect";

	public static final String MSG_NOT_FOUND = "Node not found with the param(@param) and value(@value)";
	public static final String MSG_NOT_FOUND_VERSION = "Node version not found with the nodeId(@nodeId) and version label(@version)";
	public static final String MSG_SIGN_WRITE = "Could not write the signature of @nodeId";
	public static final String MSG_CONTENT_WRITE = "Could not write the content of de @nodeId";
	public static final String MSG_MIGRATED_NODE = "El nodo (appId:@appId, extId:@externalId) ha sido migrado con el nuevo id [workspace://SpacesStore/@nodeId]";
	public static final String MSG_MIGRATED_NODE_DUPLICATED = "Se han encontrado dos nodos con el identificador (appId:@appId, extId:@externalId)";
	public static final String MSG_CHANGE_TYPE_EXCEPTION = "Can not update the node, the type of the node is not equals. Original (@originalType), update (@nodeType)";
	public static final String MSG_INTEGRITY_ASPECT = "Can not remove the mandatory aspect @aspect";
	public static final String MSG_GET_BYTE_FROM_DATAHANDLER = "Error in get byte[] from datahandler. @error";
	public static final String MSG_INVALID_NODEID ="@nodeId is not valid";
	public static final String MSG_LOCKED_NODE ="@nodeId esta bloqueado.";
	public static final String MSG_INVALID_PERMISSSION ="No es posible aplicar el permiso @permission sobre el nodo @nodeId.";
	public static final String MSG_UPDATE_NODE_IS_A_VERSION = "@id can not be modified because is a version";
	public static final String MSG_INVALID_QNAME_FORMAT = "@qname is not valid as qname";
	public static final String MSG_ALREADY_EXITS = "@node already exits in the repository";
	public static final String MSG_INVALID_PERMISSION = "Could not have the permission of @permission to perfom the operation";
	public static final String MSG_FINALLY_NODE = "Can not remove the node (@node) becuase is finally";
	public static final String MSG_INVALID_ADD_DRAFT_ASPECT = "Can not add the draft aspect to the node because is a final document";
	public static final String MSG_ERROR_INDICEELECTRONICO ="No ha sido posible generar el indice electronico del expediente cuyo nodo es @nodeId.";
	public static final String MSG_CHECK_DOCUMENTARY_SERIES_IN_CLASSIFICATION_TABLE = "The @classificationCategory is not found in the classification table";
	public static final String MSG_CONFIGURATION_ROOT_TEMPLATE = "Error in the configuration of Alfresco. The node root of the gdib templates for expedient is null ";
	public static final String MSG_AUTHORIY_NOT_EXITS = "The authority @authority not exit";
	public static final String MSG_NOT_OPEN_EXPEDIENT = "The expedient (@expdient) is not open";
	public static final String MSG_IS_A_EXCHANGE_EXPEDIENT = "The expedient (@expdient) is a exchange expedient";
	public static final String MSG_VERSION_NOT_EXITS = "The version @version not exit in the node with id (@id)";
	public static final String MSG_GENERATE_XML_GREGORIAN_CALENDAR = "Error trying to generate a XMLGregorianCalendar object";
	public static final String MSG_INVALID_MIGRATION_ID_VALUE ="In the MigrationId parameter you must come informed the csv or identifier , but not both at once";
	public static final String MSG_MIGRATION_NODE_NOT_FOUND = "Migration node not found. Search Values (appId:@appId, extId:@externalId)";
	public static final String MSG_MIGRATION_NODE_ZIP_NOT_FOUND = "ZIP Migration node not found. Search for: @node";
	public static final String MSG_MIGRATION_NODE_SIGN_NOT_FOUND = "SIGN Migration node not found. Search for: @node";
	public static final String MSG_NODE_NOT_IN_PATH = "Node (@nodeId) is not found in the path (@path)";
	public static final String MSG_CONFIGURATION_ROOT_RM = "Error in the configuration of Alfresco. The node root of RM site is null ";
	public static final String MSG_CSV = "CSV generation error";
	public static final String MSG_CHECK_METADATA_INTEGRITY = "if the medatada(@propBase) has the value (@value), the metadata(@prop) can not be null or empty";
	public static final String MSG_RESTRICTION = "Se ha fallado la validación de restriccion de GdibHeader.";
	public static final String MSG_CHECK_NO_MODIFY_PROPERTIES = "@property can not be modified because is a final node";
	public static final String MSG_ILLEGAL_LIN = "Link(copy) between different documentary series is not allowed.";
	public static final String MSG_DOCUMENTARY_SERIES_NO_DOCUMENTED = "The documentary series (@codClasificacion) is not documented";
	public static final String MSG_FILE_IS_EMPTY = "The file @fileNumber is empty.";
	public static final String MSG_FILE_INDEX_ERROR = "An error occurred while generating the index of the file @fileNumber: @exceptionMessage.";
	public static final String MSG_CHECK_NODE_TYPE_ERROR = "The node @node isn't type @type.";
	public static final String MSG_REOPEN_EXPURGATE_EXPEDIENT = "Can open a expurgate expedient (@node)";
}
