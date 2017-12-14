package es.caib.gdib.ws.iface;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import es.caib.gdib.ws.common.types.DataNodeTransform;
import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.MigrationID;
import es.caib.gdib.ws.common.types.MigrationNode;
import es.caib.gdib.ws.exception.GdibException;

@WebService(targetNamespace = "http://www.caib.es/gdib/migration/ws", name = "GdibMigrationServiceSoapPort")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface MigrationServiceSoapPort {


    @WebMethod(action = "getMigrationNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/migration/ws")
    MigrationNode getMigrationNode(
    		@WebParam( partName = "migrationId", name = "migrationId", targetNamespace="http://www.caib.es/gdib/migration/ws") MigrationID migrationId,
    		@WebParam( partName = "withContent", name = "withContent", targetNamespace="http://www.caib.es/gdib/migration/ws") boolean withContent,
    		@WebParam( partName = "withSign", name = "withSign", targetNamespace="http://www.caib.es/gdib/migration/ws") boolean withSign,
    		@WebParam( partName = "withMigrationSign", name = "withMigrationSign", targetNamespace="http://www.caib.es/gdib/migration/ws") boolean withMigrationSign,
    		@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "transformNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/migration/ws")
	String transformNode(
			@WebParam( partName = "datanodetransform", name = "datanodetransform", targetNamespace="http://www.caib.es/gdib/migration/ws") DataNodeTransform datanodetransform,
			@WebParam( partName = "fileNumber", name = "fileNumber", targetNamespace="http://www.caib.es/gdib/migration/ws") String fileNumber,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;
}
