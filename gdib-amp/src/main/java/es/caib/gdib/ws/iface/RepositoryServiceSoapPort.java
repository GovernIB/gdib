package es.caib.gdib.ws.iface;

import java.util.List;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import es.caib.gdib.ws.common.types.GdibHeader;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.NodeVersion;
import es.caib.gdib.ws.common.types.SearchResults;
import es.caib.gdib.ws.common.types.MigrationInfo;
import es.caib.gdib.ws.exception.GdibException;

@WebService(targetNamespace = "http://www.caib.es/gdib/repository/ws", name = "GdibRepositoryServiceSoapPort")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RepositoryServiceSoapPort {

	@WebMethod(action = "createNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	String createNode(
			@WebParam(partName = "node", name = "node", targetNamespace="http://www.caib.es/gdib/repository/ws") Node node,
			@WebParam(partName = "parent", name = "parent", targetNamespace="http://www.caib.es/gdib/repository/ws") String parentId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "createAndGetNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	Node createAndGetNode(
			@WebParam(partName = "node", name = "node", targetNamespace="http://www.caib.es/gdib/repository/ws") Node node,
			@WebParam(partName = "parent", name = "parent", targetNamespace="http://www.caib.es/gdib/repository/ws") String parentId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws Exception;

	@WebMethod(action = "modifyNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	void modifyNode(
			@WebParam(partName = "node", name = "node", targetNamespace="http://www.caib.es/gdib/repository/ws") Node node,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "getNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	Node getNode(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws") String nodeId,
			@WebParam(partName = "widthContent", name = "withContent", targetNamespace="http://www.caib.es/gdib/repository/ws") boolean withContent,
			@WebParam(partName = "withSign", name = "withSign", targetNamespace="http://www.caib.es/gdib/repository/ws") boolean withSign,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "moveNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	void moveNode(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws") String nodeId,
			@WebParam(partName = "newParent", name = "newParent", targetNamespace="http://www.caib.es/gdib/repository/ws") String newParent,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "searchNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	SearchResults searchNode(
			@WebParam(partName = "luceneSearch", name = "luceneSearch", targetNamespace="http://www.caib.es/gdib/repository/ws") String luceneSearch,
			@WebParam(partName = "pagina", name = "pagina", targetNamespace="http://www.caib.es/gdib/repository/ws") int pagina,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader)	throws GdibException;

	@WebMethod(action = "removeNode")
	void removeNode(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws") String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "linkNode")
	@WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws") 
	String linkNode(
			@WebParam(partName = "parentId", name = "parentId", targetNamespace="http://www.caib.es/gdib/repository/ws") String parentId,
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws") String nodeId,
			@WebParam(partName = "linkMode", name = "linkMode", targetNamespace="http://www.caib.es/gdib/repository/ws") String linkMode,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "foliateNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	DataHandler foliateNode(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws") String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "exportNode")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	DataHandler exportNode(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws") String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "getNodeVersionList")
    @WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	List<NodeVersion> getNodeVersionList(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws") String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;


	@WebMethod(action = "authorizeNode")
	void authorizeNode(
			@WebParam(partName = "nodeIds", name = "nodeIds", targetNamespace="http://www.caib.es/gdib/repository/ws") List<String> nodeIds,
			@WebParam(partName = "authorities", name = "authorities", targetNamespace="http://www.caib.es/gdib/repository/ws") List<String> authorities,
			@WebParam(partName = "permission", name = "permission", targetNamespace="http://www.caib.es/gdib/repository/ws") String permission,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "removeAuthority")
	void removeAuthority(
			@WebParam(partName = "nodeIds", name = "nodeIds", targetNamespace="http://www.caib.es/gdib/repository/ws") List<String> nodeIds,
			@WebParam(partName = "authorities", name = "authorities", targetNamespace="http://www.caib.es/gdib/repository/ws") List<String> authorities,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "lockNode")
	void lockNode(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws") String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "unlockNode")
	void unlockNode(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws")String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action = "getTicket" )
	@WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	String getTicket(
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action ="getMigrationInfo" )
	@WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	MigrationInfo getMigrationInfo(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws")String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action ="getCSV" )
	@WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	String getCSV(
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action ="openFile" )
	@WebResult(name="result", targetNamespace="http://www.caib.es/gdib/repository/ws")
	String openFile(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws")String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

	@WebMethod(action="closeFile")
	void closeFile(
			@WebParam(partName = "nodeId", name = "nodeId", targetNamespace="http://www.caib.es/gdib/repository/ws")String nodeId,
			@WebParam( partName = "gdibHeader", name = "gdibHeader", targetNamespace="http://www.caib.es/gdib/repository/ws") GdibHeader gdibHeader) throws GdibException;

}