package es.caib.gdib.utils;

import java.io.IOException;

import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.namespace.QName;

import es.caib.gdib.ws.common.types.MigrationID;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.exception.GdibExceptionInfo;
import es.caib.gdib.ws.exception.GdibTransactionException;

public class ExUtils {

	private GdibExceptionInfo getGdibExceptionInfo(final int code, final String message) {
		final GdibExceptionInfo gdibFault = new GdibExceptionInfo();
		gdibFault.setCode(code);
		gdibFault.setMessage(message);
		return gdibFault;
	}

	public GdibTransactionException transactionException(Exception e){
		return new GdibTransactionException(e.getMessage(),this.getGdibExceptionInfo(ConstantUtils.CODE_TRANSACTION_ERROR, "Error al procesar la transacción"));
	}

	public GdibException checkParamsException(String param,String value){
		String message = ConstantUtils.MSG_CHECK_PARAMS.replace("@param",param).replace("@value", value);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_PARAMS, message));
	}

	public GdibException nullParamException(String param) {
		String message = ConstantUtils.MSG_NULL_PARAM.replace("@param", param);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_NULL_PARAM, message));
	}

	public GdibException informParamException(String param) {
		String message = ConstantUtils.MSG_INFORM_PARAM.replace("@param", param);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INFORM_PARAM, message));
	}

	public GdibException nodeNotFoundException(String param, String value) {
		String message = ConstantUtils.MSG_NOT_FOUND.replace("@param", param).replace("@value", value);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_NOT_FOUND, message));
	}

	public GdibException nodeVersionNotFoundException(String nodeId, String version) {
		String message = ConstantUtils.MSG_NOT_FOUND_VERSION.replace("@nodeId", nodeId).replace("@version", version);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_NOT_FOUND_VERSION, message));
	}

	public GdibException checkMetadataException(String property) {
		String message = ConstantUtils.MSG_CHECK_PROPERTIES.replace("@property", property);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_PROPERTIES, message));
	}

	public GdibException checkMetadataValueException(String property, String value) {
		if ( property == null)property ="";
		if ( value == null)value="";
		String message = ConstantUtils.MSG_CHECK_PROPERTIES_VALUE.replace("@property", property).replace("@value", value);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_PROPERTIES_VALUE, message));
	}

	public GdibException checkMandatoryMetadataException(String property) {
		String message = ConstantUtils.MSG_CHECK_MANDATORY_PROPERTIES.replace("@property", property);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_MANDATORY_PROPERTIES, message));
	}

	public GdibException setSignException(String nodeId, IOException exception) {
		String message = ConstantUtils.MSG_SIGN_WRITE.replace("@nodeId",nodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_SIGN_WRITE, message), exception);
	}

	public GdibException setContentException(String nodeId) {
		return setContentException(nodeId, null);
	}

	public GdibException setContentException(String nodeId, IOException exception) {
		String message = ConstantUtils.MSG_CONTENT_WRITE.replace("@nodeId",nodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CONTENT_WRITE, message), exception);
	}

	public GdibException migratedNodeException( String appId, String externalId, String nodeId ){
		String message = ConstantUtils.MSG_MIGRATED_NODE.replace("@appId", appId).replace("@externalId", externalId).replace("@nodeId", nodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_MIGRATION_NODE, message));
	}

	public GdibException migratedNodeExceptionDuplicated( String appId, String externalId){
		String message = ConstantUtils.MSG_MIGRATED_NODE_DUPLICATED.replace("@appId", appId).replace("@externalId", externalId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_MIGRATED_NODE_DUPLICATED, message));
	}

	public GdibException invalidContent(String nodeId, ContentIOException exception) {
		String message = ConstantUtils.MSG_NODE_INVALID_CONTENT.replace("@nodeId", nodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_NODE_INVALID_CONTENT, message), exception);
	}

	public GdibException invalidAspectExcepcion(String aspect) {
		String message = ConstantUtils.MSG_INVALID_ASPECT.replace("@aspect", aspect);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INVALID_ASPECT, message));
	}

	public GdibException changeTypeException(String nodeType, String originalType) {
		String message = ConstantUtils.MSG_CHANGE_TYPE_EXCEPTION.replace("@nodeType", nodeType).replace("@originalType", originalType);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHANGE_TYPE_EXCEPTION, message));
	}

	public GdibException integrityAspectException(String aspect, IntegrityException exception) {
		String message = ConstantUtils.MSG_INTEGRITY_ASPECT.replace("@aspect", aspect);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INTEGRITY_ASPECT, message), exception);
	}

	public GdibException getByteFromDataHandlerException(String info, IOException exception) {
		String message = ConstantUtils.MSG_GET_BYTE_FROM_DATAHANDLER.replace("@error", info);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_GET_BYTE_FROM_DATAHANDLER, message), exception);
	}

	public GdibException isAVersionException(String id) {
		String message = ConstantUtils.MSG_UPDATE_NODE_IS_A_VERSION.replace("@id", id);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_UPDATE_NODE_IS_A_VERSION, message));
	}

	public GdibException invalidQnameException(String qname) {
		String message = ConstantUtils.MSG_INVALID_QNAME_FORMAT.replace("@qname", qname);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INVALID_QNAME_FORMAT, message));
	}

	public GdibException invalidTypeException(String qname) {
		String message = ConstantUtils.MSG_CHECK_TYPE.replace("@type", qname);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_TYPE, message));
	}

	public GdibException invalidNodeId(String nodeId) {
		String message = ConstantUtils.MSG_INVALID_NODEID.replace("@nodeId", nodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INVALID_NODEID, message));
	}

	public GdibException lockedNode(String nodeId) {
		String message = ConstantUtils.MSG_LOCKED_NODE.replace("@nodeId", nodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_LOCKED_NODE, message));
	}

	public GdibException invalidPermission(String permission, String nodeId) {
		String message = ConstantUtils.MSG_INVALID_PERMISSSION.replace("@permission", permission);
		message = message.replace("@nodeId", nodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INVALID_PERMISSSION, message));
	}

	public GdibException errorBuildIndiceElectronicoOfExpediente(String expedienteNodeId, Exception e){
		String message = ConstantUtils.MSG_ERROR_INDICEELECTRONICO.replace("@nodeId", expedienteNodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_ERROR_INDICEELECTRONICO, message));
	}

	public GdibException nodeAlreadyExists(String nodeId) {
		String message = ConstantUtils.MSG_ALREADY_EXITS.replace("@node", nodeId);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_ALREADY_EXITS, message));
	}

	public GdibException invalidPermissionException(String permission) {
		String message = ConstantUtils.MSG_INVALID_PERMISSION.replace("@permission", permission);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INVALID_PERMISSION, message));
	}

	public GdibException finalDocumentException(String node) {
		String message = ConstantUtils.MSG_FINALLY_NODE.replace("@node", node);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_FINALLY_NODE, message));
	}

	public GdibException invalidAddDraftAspectExcepcion() {
		String message = ConstantUtils.MSG_INVALID_ADD_DRAFT_ASPECT;
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INVALID_ADD_DRAFT_ASPECT, message));
	}

	public GdibException checkDocumentarySeriesInClassificationTableExcepcion(String classificationCategory) {
		String message = ConstantUtils.MSG_CHECK_DOCUMENTARY_SERIES_IN_CLASSIFICATION_TABLE.replace("@classificationCategory", classificationCategory);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_DOCUMENTARY_SERIES_IN_CLASSIFICATION_TABLE, message));
	}

	public GdibException configurationRootTemplateException() {
		String message = ConstantUtils.MSG_CONFIGURATION_ROOT_TEMPLATE;
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CONFIGURATION_ROOT_TEMPLATE, message));
	}

	public GdibException configurationRootRMException() {
		String message = ConstantUtils.MSG_CONFIGURATION_ROOT_RM;
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CONFIGURATION_ROOT_RM, message));
	}

	public GdibException authorityNotExitsException(String authority) {
		String message = ConstantUtils.MSG_AUTHORIY_NOT_EXITS;
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_AUTHORIY_NOT_EXITS, message));
	}

	public GdibException notOpenExpedientException(String expedient) {
		String message = ConstantUtils.MSG_NOT_OPEN_EXPEDIENT.replace("@expdient", expedient);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_NOT_OPEN_EXPEDIENT, message));
	}

	public GdibException isExchangeExpedientException(String expedient) {
		String message = ConstantUtils.MSG_IS_A_EXCHANGE_EXPEDIENT.replace("@expdient", expedient);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_IS_A_EXCHANGE_EXPEDIENT, message));
	}

	public GdibException versionNotExitsException(String version, String id) {
		String message = ConstantUtils.MSG_VERSION_NOT_EXITS.replace("@version", version).replace("@id", id);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_VERSION_NOT_EXITS, message));
	}

	public GdibException generateXMLGregorianCalendarErrorException() {
		String message = ConstantUtils.MSG_GENERATE_XML_GREGORIAN_CALENDAR;
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_GENERATE_XML_GREGORIAN_CALENDAR, message));
	}

	public GdibException migrationNodeNotFoundException(MigrationID migrationId) {
		String message = ConstantUtils.MSG_MIGRATION_NODE_NOT_FOUND.replace("@appId", migrationId.getAppId()).replace("@externalId", migrationId.getExternalId());
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_MIGRATION_NODE_NOT_FOUND, message));
	}

	public GdibException nodeNotInPath(String nodeId, String path){
		String message = ConstantUtils.MSG_NODE_NOT_IN_PATH.replace("@nodeId", nodeId).replace("@path", path);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_NODE_NOT_IN_PATH, message));
	}

	public GdibException csvError(Exception e) {
		return new GdibException( ConstantUtils.MSG_CSV, e);
	}

	public GdibException checkMetadataIntegrityException(QName propBase, String propBaseValue, QName prop) {
		String message = ConstantUtils.MSG_CHECK_METADATA_INTEGRITY.replace("@propBase", propBase.toString()).replace("@value", propBaseValue).replace("@prop",prop.toString());
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_METADATA_INTEGRITY, message));
	}

	public GdibException checkRestrictionException() {
		return new GdibException ( ConstantUtils.MSG_RESTRICTION );
	}

	public GdibException checkNoModifyMetadataException(String property) {
		String message = ConstantUtils.MSG_CHECK_NO_MODIFY_PROPERTIES.replace("@property", property);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_NO_MODIFY_PROPERTIES, message));
	}

	public GdibException illegalLinkException() {
		String message = ConstantUtils.MSG_ILLEGAL_LIN;
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_METADATA_INTEGRITY, message));
	}

	public GdibException documentarySeriesNoDocumentedException(String codClasificacion) {
		String message = ConstantUtils.MSG_DOCUMENTARY_SERIES_NO_DOCUMENTED.replace("@codClasificacion", codClasificacion);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_DOCUMENTARY_SERIES_NO_DOCUMENTED, message));
	}

	public GdibException fileIsEmptyException(String fileNumber) {
		String message = ConstantUtils.MSG_FILE_IS_EMPTY.replace("@fileNumber", fileNumber);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_FILE_IS_EMPTY, message));
	}

	public GdibException generateFileIndexException(String fileNumber, String exceptionMessage) {
		String message = ConstantUtils.MSG_FILE_INDEX_ERROR.replace("@fileNumber", fileNumber).replace("@exceptionMessage", exceptionMessage);
		return new GdibException( message, this.getGdibExceptionInfo(ConstantUtils.CODE_INDEX_GEN_ERROR, message));
	}

	public GdibException firmaMigracionNotFound(String node) {
		String message = ConstantUtils.MSG_MIGRATION_NODE_SIGN_NOT_FOUND.replace("@node", node);
		return new GdibException(message,this.getGdibExceptionInfo(ConstantUtils.CODE_MIGRATION_NODE, message) );
	}

	public GdibException zipMigracionNotFound(String node) {
		String message = ConstantUtils.MSG_MIGRATION_NODE_ZIP_NOT_FOUND.replace("@node", node);
		return new GdibException(message, this.getGdibExceptionInfo(ConstantUtils.CODE_MIGRATION_NODE, message));
	}

	public GdibException reopenExpurgateExpedientError(String node) {
		String message = ConstantUtils.MSG_REOPEN_EXPURGATE_EXPEDIENT.replace("@node", node);
		return new GdibException(message, this.getGdibExceptionInfo(ConstantUtils.CODE_REOPEN_EXPURGATE_EXPEDIENT, message));
	}

	public GdibException luceneQueryParserException(String lucene) {
		String message = ConstantUtils.MSG_LUCENE_QUERY_PARSER_EXCEPTION.replace("@lucene", lucene);
		return new GdibException(message, this.getGdibExceptionInfo(ConstantUtils.CODE_CHECK_PARAMS, message));
	}

	public GdibException genericException(String whilethe){
		String message = ConstantUtils.MSG_GENERIC_ERROR.replace("@whilethe",whilethe);
		return new GdibException(message, this.getGdibExceptionInfo(ConstantUtils.CODE_GENERAL, message));
	}
}
