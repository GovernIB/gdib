package es.caib.gdib.rm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.alfresco.repo.exporter.NodeContentData;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.view.ExportPackageHandler;

public class RMExportPackageHandler implements ExportPackageHandler {
	
	protected OutputStream XML_outputStream;
	private ArrayList<NodeRef> listNodeRefsToMove;
	
	@Override
	public OutputStream createDataStream() {
		XML_outputStream = new ByteArrayOutputStream(); 
		
		return XML_outputStream;
	}

	@Override
	public void endExport() {
		try {
			XML_outputStream.close();
		} catch (IOException e) {
			// TODO ¿Como subimos esta excepcion para arriba?
			e.printStackTrace();
		}
	}

	@Override
	public ContentData exportContent(InputStream content, ContentData contentData) {
		if (contentData instanceof NodeContentData){
			NodeContentData nodeContentData = (NodeContentData) contentData;
			NodeRef nodeRef = nodeContentData.getNodeRef();
			
			// TODO Tener en cuenta los expedientes enlazados => igual hace falta crear un aspecto con un porpiedad nultiple para guardar los uid de los exp. enlazados.
			if (!listNodeRefsToMove.contains(nodeRef)) {
				listNodeRefsToMove.add(nodeRef);
			}
		}
		
		return contentData;
	}

	@Override
	public void startExport() {
	    listNodeRefsToMove = new ArrayList<NodeRef>();
	}

	public ArrayList<NodeRef> getListNodeRefsToMove() {
		return listNodeRefsToMove;
	}

	public void setListNodeRefsToMove(ArrayList<NodeRef> listNodeRefsToMove) {
		this.listNodeRefsToMove = listNodeRefsToMove;
	}

	public OutputStream getXML_outputStream() {
		return XML_outputStream;
	}

	public void setXML_outputStream(OutputStream xML_outputStream) {
		XML_outputStream = xML_outputStream;
	}
}
