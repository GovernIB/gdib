package es.caib.arxiudigital.apirest.constantes;

public abstract class Servicios {
	
	// Serveis sobre expedients
	public static final String CLOSE_FILE 			= "/services/closeFile";
	public static final String CANCEL_PERMISOS_FILE = "/services/cancelPermissionsOnFiles";
	public static final String CREATE_FILE 			= "/services/createFile";
	public static final String EASY_SEARH_FILE		= "/services/easyFileSearch";
	public static final String EXPORT_FILE 			= "/services/exportFile";
	public static final String GENERAR_FILE_INDEX	= "/services/generateFileIndex";
	public static final String GET_FILE 			= "/services/getFile";
	public static final String GET_VERSION_FILE 	= "/services/getFileVersionList";
	public static final String GRANT_PERMISOS_FILE 	= "/services/grantPermissionsOnFiles";
	public static final String LINK_FILE 			= "/services/linkFile";
	public static final String LOCK_FILE 			= "/services/lockFile";
	public static final String REMOVE_FILE 			= "/services/removeFile";
	public static final String SEARCH_FILE 			= "/services/fileSearch";
	public static final String REOPEN_FILE 			= "/services/reopenFile";
	public static final String SET_FILE 			= "/services/setFile";
	public static final String UNLOCK_FILE 			= "/services/unlockFile";
	
	// Serveis sobre subexpedients
	public static final String CREATE_CHILD_FILE 	= "/services/createChildFile";
	public static final String MOVE_CHILD_FILE 		= "/services/moveChildFile";
	
	// Serveis sobre documents
	public static final String CANCEL_PERMISOS_DOC 	= "/services/cancelPermissionsOnDocs";
	public static final String COPY_DOC 			= "/services/copyDocument";
	public static final String CREATE_DOC 			= "/services/createDocument";
	public static final String CREATE_DRAFT 		= "/services/createDraftDocument";
	public static final String DISPATCH_DOC 		= "/services/dispatchDocument";
	public static final String EASY_SEARH_DOC		= "/services/easyDocumentSearch";
	public static final String GET_DOC				= "/services/getDocument";
	public static final String GET_VERSION_DOC		= "/services/getDocVersionList";
	public static final String GET_ENIDOC			= "/services/getENIDocument";
	public static final String GRANT_PERMISOS_DOC 	= "/services/grantPermissionsOnDocs";
	public static final String LINK_DOC				= "/services/linkDocument";
	public static final String LOCK_DOC				= "/services/lockDocument";
	public static final String MOVE_DOC				= "/services/moveDocument";
	public static final String SEARCH_DOC			= "/services/documentSearch";
	public static final String SET_DOC				= "/services/setDocument";
	public static final String SET_FINAL_DOC		= "/services/setFinalDocument";
	public static final String UNLOCK_DOC			= "/services/unlockDocument";
	public static final String VALIDATE_DOC			= "/services/validateDocument";
	public static final String REMOVE_DOC			= "/services/removeDocument";
	
	//Serveis sobre el CSV
	public static final String GENERATE_CSV 	= "/services/generateDocCSV";
	
	//Serveis sobre Folders
	public static final String CANCEL_PERMISOS_FOLDER 	= "/services/cancelPermissionsOnFolders";
	public static final String CREATE_FOLDER 			= "/services/createFolder";
	public static final String LINK_FOLDER 				= "/services/linkFolder";
	public static final String LOCK_FOLDER 				= "/services/lockFolder";
	public static final String MOVE_FOLDER 				= "/services/moveFolder";
	public static final String SET_FOLDER 				= "/services/setFolder";
	public static final String GET_FOLDER 				= "/services/getFolder";
	public static final String GRANT_PERMISOS_FOLDER 	= "/services/grantPermissionsOnFolders";
	public static final String REMOVE_FOLDER 			= "/services/removeFolder";
	public static final String UNLOCK_FOLDER 			= "/services/unlockFolder";
	
}
