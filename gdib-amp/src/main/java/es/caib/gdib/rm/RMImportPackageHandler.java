package es.caib.gdib.rm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.view.ImportPackageHandler;
import org.alfresco.service.cmr.view.ImporterException;

import es.caib.gdib.utils.ConstantUtils;

public class RMImportPackageHandler implements ImportPackageHandler {

	private NodeService nodeService;
	private ContentService contentService;
	private String sourceDir; // ${dir.contentstore}

	private NodeRef root;

	@Override
	public void startImport() {
		// encontrar el zip de importar
	}

	@Override
	public Reader getDataStream() {
		// tengo que devolver un Reader del XML donde esta la informacion para importar
		String expedientName = (String)nodeService.getProperty(root, ConstantUtils.PROP_NAME);

		// Busco dentro del expediente. El fichero XML. Que tiene el nombre del expediente
		List<ChildAssociationRef> listNodes = nodeService.getChildAssocs(root);
		for (ChildAssociationRef childAssoc : listNodes) {
			NodeRef son = childAssoc.getChildRef();
			String sonName = (String)nodeService.getProperty(son, ConstantUtils.PROP_NAME);
			if(sonName.contains(expedientName)){
				ContentReader reader = contentService.getReader(son, ConstantUtils.PROP_CONTENT);
				Reader inputReader = new InputStreamReader(reader.getContentInputStream());
				return new BufferedReader(inputReader);
			}
		}
        return null;
	}

	@Override
	public InputStream importStream(String content) {
		// Recibo la url del contenido que tengo que importar

		File fileURL = new File(content);
        if (fileURL.isAbsolute() == false)
        {
        	// D:/Alfresco.5.1.1.4/alf_data/contentstore
        	// store://2017/3/2/10/32/2dc5aec4-173c-4e19-9994-5c1688bf6096.bin
        	content = content.replace("store:/", "");
            fileURL = new File(sourceDir, content);
        }
        try
        {
            return new FileInputStream(fileURL);
        }
        catch(IOException e)
        {
            throw new ImporterException("Failed to read content url " + content + " from file " + fileURL.getAbsolutePath());
        }
	}

	@Override
	public void endImport() {
		// cerrar el zipfile
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setRoot(NodeRef root) {
		this.root = root;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

}
