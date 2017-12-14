package es.caib.gdib.rm.utils;

import java.io.Serializable;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.gdib.utils.iface.CaibConstraintsUtilsInterface;
import es.caib.gdib.utils.iface.EniModelUtilsInterface;

public class ExpurgoUtils extends BaseScopableProcessorExtension implements EniModelUtilsInterface,CaibConstraintsUtilsInterface{

	@Autowired
	NodeService nodeService;

	public void expurgar(String nodeId){
		NodeRef ref = new NodeRef(nodeId);
		String prop = nodeService.getProperty(ref, PROP_ESTADO_ARCHIVO_QNAME).toString();
		if (ESTADO_ARCHIVO_PENDIENTE_ELIMINACION_TOTAL.equals(prop)){
			nodeService.deleteNode(ref);
		}else if (ESTADO_ARCHIVO_PENDIENTE_ELIMINACION_PARCIAL.equals(prop)){
			nodeService.setProperty(ref, PROP_ESTADO_ARCHIVO_QNAME, ESTADO_ARCHIVO_ELIMINADO);
			for(ChildAssociationRef childRef:nodeService.getChildAssocs(ref)){
				NodeRef child = childRef.getChildRef();
				Serializable subtipo = nodeService.getProperty(child, PROP_SUBTIPO_DOC_QNAME);
				if ( subtipo == null || "".equals(subtipo.toString()) ){
					nodeService.deleteNode(child);
				}
			}
		}
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
}
