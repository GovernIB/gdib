package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.version.VersionService;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.caib.gdib.utils.AdministrativeProcessingIndexSignerFactory;
import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.FoliateUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.TestUtils;
import es.caib.gdib.ws.common.types.IndiceElectronicoManager;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoCarpetaIndizada;
import es.caib.gdib.ws.xsd.expediente.indice.TipoContenidoExpediente;
import es.caib.gdib.ws.xsd.expediente.indice.TipoIndiceElectronico;

@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/repositoryService-foliateExportTest-context.xml")
public class RepositoryServiceFoliateExportTest {

    static Logger log = Logger.getLogger(RepositoryServiceFoliateExportTest.class);

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

    @Autowired
    private FoliateUtils foliateUtils;

    @Autowired
    private IndiceElectronicoManager indiceElectronicoManager;

    @Autowired
    @Qualifier("VersionService")
    private VersionService versionService;

    @Autowired
    @Qualifier("SearchService")
    private SearchService searchService;

    @Autowired
    private Node nodeDocument;
    @Autowired
    private Node nodeExpedientFoliate;
    @Autowired
    private Node agrupationNode;

    @Before
    public void configureUp() throws FileNotFoundException, NotSupportedException, SystemException, GdibException{
    	// preparo entorno de pruebas
    	testUtils.configureUp();
    	utils.setRootDM(TestUtils.rootDM.getId());
    	utils.setRootCT(TestUtils.rootCT.getId());
    	utils.setRootTemplate(TestUtils.rootTemplate.getId());
    }

    @After
    public void configureDown() throws IllegalStateException, SecurityException, SystemException{
    	testUtils.configureDown();
    }

    @Test
    public void testFilterNodesFromSimpleExpediente() throws GdibException{
    	// expediente
    	String expId = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);

    	// documentos del expediente
    	nodeDocument.setName("doc01.txt");
    	String doc01 = repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	nodeDocument.setName("doc02.txt");
    	String doc02 = repositoryServiceSoap.createNode(nodeDocument, expId, null);

    	TipoIndiceElectronico indice = foliateUtils.getContentFile(utils.toNodeRef(expId));
    	assertEquals(doc01, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(1).getId());
    	assertEquals(doc02, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(0).getId());
    }

    @Test
    public void testFilterNodesFromExpedienteWithAgrupations() throws GdibException{
    	// expediente
    	String expId = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);

    	// documentos del expediente
    	nodeDocument.setName("doc01.txt");
    	String doc01 = repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	nodeDocument.setName("doc02.txt");
    	String doc02 = repositoryServiceSoap.createNode(nodeDocument, expId, null);

    	// agrupacion con dos documentos
    	agrupationNode.setName("folder1");
    	String folder1 = repositoryServiceSoap.createNode(agrupationNode, expId, null);
    	nodeDocument.setName("doc11.txt");
    	String doc11 = repositoryServiceSoap.createNode(nodeDocument, folder1, null);
    	nodeDocument.setName("doc12.txt");
    	String doc12 = repositoryServiceSoap.createNode(nodeDocument, folder1, null);

    	TipoIndiceElectronico indice = foliateUtils.getContentFile(utils.toNodeRef(expId));
    	
    	assertEquals(doc01, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(1).getId());
    	assertEquals(doc02, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(0).getId());

    	TipoContenidoCarpetaIndizada contenidoFolder1 = indice.getContenidoExpediente().getContenido().getCarpetasIndizadas().getCarpetaIndizada().get(0);
    	assertEquals(doc11, contenidoFolder1.getDocumentosIndizados().getDocIndizado().get(1).getId());
    	assertEquals(doc12, contenidoFolder1.getDocumentosIndizados().getDocIndizado().get(0).getId());
    }

    @Test
    public void testFilterNodesFromExpedienteWithNestedAgrupations() throws GdibException{
    	// expediente
    	String expId = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);

    	// documentos del expediente
    	nodeDocument.setName("doc01.txt");
    	String doc01 = repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	nodeDocument.setName("doc02.txt");
    	String doc02 = repositoryServiceSoap.createNode(nodeDocument, expId, null);

    	// agrupacion con dos documentos
    	agrupationNode.setName("folder1");
    	String folder1 = repositoryServiceSoap.createNode(agrupationNode, expId, null);
    	nodeDocument.setName("doc11.txt");
    	String doc11 = repositoryServiceSoap.createNode(nodeDocument, folder1, null);
    	nodeDocument.setName("doc12.txt");
    	String doc12 = repositoryServiceSoap.createNode(nodeDocument, folder1, null);

    	// agrupacion anidada con dos documentos
    	agrupationNode.setName("folderNested1");
    	String folderNested1 = repositoryServiceSoap.createNode(agrupationNode, folder1, null);
    	nodeDocument.setName("docfolderNested1.txt");
    	String docfolderNested11 = repositoryServiceSoap.createNode(nodeDocument, folderNested1, null);
    	nodeDocument.setName("docfolderNested12.txt");
    	String docfolderNested12 = repositoryServiceSoap.createNode(nodeDocument, folderNested1, null);

    	TipoIndiceElectronico indice = foliateUtils.getContentFile(utils.toNodeRef(expId));
    	
    	assertEquals(doc01, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(1).getId());
    	assertEquals(doc02, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(0).getId());

    	TipoContenidoCarpetaIndizada contenidoFolder1 = indice.getContenidoExpediente().getContenido().getCarpetasIndizadas().getCarpetaIndizada().get(0);
    	assertEquals(doc11, contenidoFolder1.getDocumentosIndizados().getDocIndizado().get(1).getId());
    	assertEquals(doc12, contenidoFolder1.getDocumentosIndizados().getDocIndizado().get(0).getId());

    	TipoContenidoCarpetaIndizada contenidoFolderNested1 = contenidoFolder1.getCarpetasIndizadas().getCarpetaIndizada().get(0);
    	assertEquals(docfolderNested11, contenidoFolderNested1.getDocumentosIndizados().getDocIndizado().get(1).getId());
    	assertEquals(docfolderNested12, contenidoFolderNested1.getDocumentosIndizados().getDocIndizado().get(0).getId());
    }

    @Test
    @DirtiesContext
    public void testFilterNodesFromExpedienteWithAgrupationsAndLink() throws GdibException{
    	// expediente
    	String expId = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);

    	// documentos del expediente
    	nodeDocument.setName("doc01.txt");
    	String doc01 = repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	nodeDocument.setName("doc02.txt");
    	String doc02 = repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	// documento linkado al expediente
    	nodeDocument.setName("docLink01.txt");
    	String docLink01 = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.linkNode(expId, docLink01, "reference", null);

    	// agrupacion con dos documentos y documento linkado
    	agrupationNode.setName("folder1");
    	String folder1 = repositoryServiceSoap.createNode(agrupationNode, expId, null);
    	nodeDocument.setName("doc11.txt");
    	String doc11 = repositoryServiceSoap.createNode(nodeDocument, folder1, null);
    	nodeDocument.setName("doc12.txt");
    	String doc12 = repositoryServiceSoap.createNode(nodeDocument, folder1, null);
    	nodeDocument.setName("docLink11.txt");
    	String docLink02 = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.linkNode(folder1, docLink02, "reference", null);

    	// expediente linkado
    	nodeExpedientFoliate.setName("expedienteLink2");
    	nodeExpedientFoliate.getProperties().add(new Property(ConstantUtils.PROP_COD_CLASIFICACION_QNAME, "EXP001235"));
    	String expLinkId = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);
    	repositoryServiceSoap.linkNode(expId, expLinkId, "reference", null);

    	TipoIndiceElectronico indice = foliateUtils.getContentFile(utils.toNodeRef(expId));
    	
    	assertEquals(doc01, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(1).getId());
    	assertEquals(doc02, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(0).getId());
    	assertEquals(docLink01, indice.getContenidoExpediente().getContenido().getDocumentosReferenciados().getDocReferenciado().get(0).getId());

    	TipoContenidoCarpetaIndizada contenidoFolder1 = indice.getContenidoExpediente().getContenido().getCarpetasIndizadas().getCarpetaIndizada().get(0);
    	assertEquals(doc11, contenidoFolder1.getDocumentosIndizados().getDocIndizado().get(1).getId());
    	assertEquals(doc12, contenidoFolder1.getDocumentosIndizados().getDocIndizado().get(0).getId());
    	assertEquals(docLink02, contenidoFolder1.getDocumentosReferenciados().getDocReferenciado().get(0).getId());

    	assertEquals(expLinkId, indice.getContenidoExpediente().getContenido().getExpedientesReferenciados().getExpReferenciado().get(0).getId());
    }

    @Test
    public void testFilterNodesFromExpedienteWithSubExpedient() throws GdibException{
    	// expediente
    	String expId = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);
    	nodeDocument.setName("doc11.txt");
    	String doc11 = repositoryServiceSoap.createNode(nodeDocument, expId, null);

    	// subexpediente con un documento
    	nodeExpedientFoliate.setName("expediente2");
    	String expId2 = repositoryServiceSoap.createNode(nodeExpedientFoliate, expId, null);
    	nodeDocument.setName("doc21.txt");
    	String doc21 = repositoryServiceSoap.createNode(nodeDocument, expId2, null);

    	// agrupacion dentro del subexpediente con un documento
    	agrupationNode.setName("folder1");
    	String folder1 = repositoryServiceSoap.createNode(agrupationNode, expId2, null);
    	nodeDocument.setName("doc311.txt");
    	String doc211 = repositoryServiceSoap.createNode(nodeDocument, folder1, null);

    	TipoIndiceElectronico indice = foliateUtils.getContentFile(utils.toNodeRef(expId));
    	
    	assertEquals(doc11, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(0).getId());

    	TipoContenidoExpediente contenidoExpediente2 = indice.getContenidoExpediente().getContenido().getSubexpedientesIndizados().getSubexpediente().get(0).getContenido();
    	assertEquals(doc21, contenidoExpediente2.getDocumentosIndizados().getDocIndizado().get(0).getId());

    	TipoContenidoCarpetaIndizada contenidoExpediente2Folder1 = contenidoExpediente2.getCarpetasIndizadas().getCarpetaIndizada().get(0);
    	assertEquals(doc211, contenidoExpediente2Folder1.getDocumentosIndizados().getDocIndizado().get(0).getId());
    }

    @Test
    public void testFilterNodesFromExpedienteWithSubExpedientInAgrupation() throws GdibException{
    	// expediente
    	String expId = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);
    	nodeDocument.setName("docExpedient1.txt");
    	String docExpedient1 = repositoryServiceSoap.createNode(nodeDocument, expId, null);

    	// agrupacion dentro del expediente con un documento
    	agrupationNode.setName("folderExpedient");
    	String folderExpedient = repositoryServiceSoap.createNode(agrupationNode, expId, null);
    	nodeDocument.setName("docFolderExpedient1.txt");
    	String docFolderExpedient1 = repositoryServiceSoap.createNode(nodeDocument, folderExpedient, null);

    	// dentro de la carpeta, un subexpediente con un documento
    	nodeExpedientFoliate.setName("expediente2");
    	String expId2 = repositoryServiceSoap.createNode(nodeExpedientFoliate, folderExpedient, null);
    	nodeDocument.setName("doc21.txt");
    	String doc21 = repositoryServiceSoap.createNode(nodeDocument, expId2, null);

    	// y dentro del subexpediente otra carpeta
    	agrupationNode.setName("folderSubExpedient");
    	String folderSubExpedient = repositoryServiceSoap.createNode(agrupationNode, expId2, null);
    	nodeDocument.setName("docFolderExpedient1.txt");
    	String docFolderSubExpedient1 = repositoryServiceSoap.createNode(nodeDocument, folderSubExpedient, null);

    	TipoIndiceElectronico indice = foliateUtils.getContentFile(utils.toNodeRef(expId));
    	
    	assertEquals(docExpedient1, indice.getContenidoExpediente().getContenido().getDocumentosIndizados().getDocIndizado().get(0).getId());

    	TipoContenidoCarpetaIndizada contenidoFolder = indice.getContenidoExpediente().getContenido().getCarpetasIndizadas().getCarpetaIndizada().get(0);
    	assertEquals(docFolderExpedient1, contenidoFolder.getDocumentosIndizados().getDocIndizado().get(0).getId());

    	TipoContenidoExpediente contenidoFolderExpediente2 = contenidoFolder.getSubexpedientes().getSubexpediente().get(0).getContenido();
    	assertEquals(doc21, contenidoFolderExpediente2.getDocumentosIndizados().getDocIndizado().get(0).getId());

    	TipoContenidoCarpetaIndizada contenidoFolderExpediente2Folder = contenidoFolderExpediente2.getCarpetasIndizadas().getCarpetaIndizada().get(0);
    	assertEquals(docFolderSubExpedient1, contenidoFolderExpediente2Folder.getDocumentosIndizados().getDocIndizado().get(0).getId());
    }

    @Test
    @DirtiesContext
    public void testGenerateIndiceElectronico() throws GdibException, DatatypeConfigurationException{
    	String expName = "ExpendienteTestFoliado01";

    	// Expediente
    	_internal_test_generaExpediente(expName);

    	StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    	ResultSet rs = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, "@cm\\:name:" + expName);

    	if (rs.length() == 0){
    		throw new AlfrescoRuntimeException("No se ha encontrado al expediente");
    	}

    	NodeRef expNodeRef = rs.getNodeRef(0);
    	TipoIndiceElectronico ie = indiceElectronicoManager.getIndiceElectronico(expNodeRef);
    	byte[] ie_bytes = indiceElectronicoManager.signXmlIndex(ie,
				AdministrativeProcessingIndexSignerFactory.CAIB_INDEX_V10, null);


    	System.out.println(ie_bytes);

    }

    private void _internal_test_generaExpediente(String nameExp) throws GdibException{
    	//*****************
    	//CREAMOS EL EXP
    	//*****************
    	List<Property> props = new ArrayList<Property>();

    	String fechaInicio = "2014-01-01T00:00:00";
    	props.add(new Property(ConstantUtils.PROP_FECHA_INICIO_QNAME, fechaInicio));
    	String fechaFin = "2014-12-31T00:00:00";
    	props.add(new Property(ConstantUtils.PROP_FECHA_INICIO_QNAME, fechaFin));

    	nodeExpedientFoliate.setName(nameExp);
    	nodeExpedientFoliate.getProperties().addAll(props);

    	String expId = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);

    	//*****************
    	//CREAMOS SUS DOCS
    	//*****************
    	nodeDocument.setName("doc_01.txt");
    	repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	nodeDocument.setName("doc_02.txt");
    	repositoryServiceSoap.createNode(nodeDocument, expId, null);
    	nodeDocument.setName("doc_03.txt");
    	repositoryServiceSoap.createNode(nodeDocument, expId, null);

    	//*****************
    	//CREAMOS SUS CARPETAS
    	//*****************
    	agrupationNode.setName("folder_1");
    	String folder_1 = repositoryServiceSoap.createNode(agrupationNode, expId, null);
    	nodeDocument.setName("doc_11.txt");
    	repositoryServiceSoap.createNode(nodeDocument, folder_1, null);
    	nodeDocument.setName("doc_12.txt");
    	repositoryServiceSoap.createNode(nodeDocument, folder_1, null);

    	agrupationNode.setName("folder_2");
    	String folder_2 = repositoryServiceSoap.createNode(agrupationNode, expId, null);
    	nodeDocument.setName("doc_21.txt");
    	repositoryServiceSoap.createNode(nodeDocument, folder_2, null);
    	nodeDocument.setName("doc_22.txt");
    	repositoryServiceSoap.createNode(nodeDocument, folder_2, null);

    	//*****************
    	//CREAMOS SUS SUBEXPEDIENTES
    	//*****************
    	nodeExpedientFoliate.setName("subexpediente1");
    	String subExpId1 = repositoryServiceSoap.createNode(nodeExpedientFoliate, expId+"/Hospital/EXP001234/2016/04/01", null);
    	nodeDocument.setName("doc_1_01.txt");
    	repositoryServiceSoap.createNode(nodeDocument, subExpId1, null);
    	agrupationNode.setName("folder_1_1");
    	String folder_1_1 = repositoryServiceSoap.createNode(agrupationNode, subExpId1, null);
    	nodeDocument.setName("doc_1_11.txt");
    	repositoryServiceSoap.createNode(nodeDocument, folder_1_1, null);

    	//*****************
    	//CREAMOS SUS DOCS ENLAZADOS
    	//*****************
    	nodeDocument.setName("docLink_01.txt");
    	String docLink01 = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.linkNode(expId, docLink01, "reference", null);
    	nodeDocument.setName("docLink_02.txt");
    	String docLink02 = repositoryServiceSoap.createNode(nodeDocument, TestUtils.rootDM.getId(), null);
    	repositoryServiceSoap.linkNode(expId, docLink02, "reference", null);

    	//*****************
    	//CREAMOS SUS EXP ENLAZADOS
    	//*****************
    	nodeExpedientFoliate.setName("ExpedienteLinkedTestFoliate_01");
    	String expLinkId_1 = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);
    	repositoryServiceSoap.linkNode(expId, expLinkId_1, "reference", null);
    	nodeExpedientFoliate.setName("ExpedienteLinkedTestFoliate_02");
    	String expLinkId_2 = repositoryServiceSoap.createNode(nodeExpedientFoliate, null, null);
    	repositoryServiceSoap.linkNode(expId, expLinkId_2, "reference", null);
    }
}

