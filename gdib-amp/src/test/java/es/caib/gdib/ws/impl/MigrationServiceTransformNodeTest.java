package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.TestUtils;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.DataNodeTransform;
import es.caib.gdib.ws.common.types.MigrationID;
import es.caib.gdib.ws.common.types.MigrationNode;
import es.caib.gdib.ws.common.types.Node;
import es.caib.gdib.ws.common.types.Property;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.MigrationServiceSoapPort;
import es.caib.gdib.ws.iface.RepositoryServiceSoapPort;


@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/migrationService-context.xml")
public class MigrationServiceTransformNodeTest {

    static Logger log = Logger.getLogger(MigrationServiceTransformNodeTest.class);

    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";

    @Autowired
    private MigrationServiceSoapPort migrationServiceSoap;

    @Autowired
    private RepositoryServiceSoapPort repositoryServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

    @Autowired
    @Qualifier("NodeService")
    private NodeService nodeService;

    @Autowired
    private Node nodeExpedient;

    @Autowired
    private Content contentType;
    @Autowired
    private DataHandler contentDataHandlerFirma;
    @Autowired
    private DataHandler contentDataHandlerFirmaMigracion;
    @Autowired
    private DataHandler contentDataHandlerZipMigracion;

    @Before
    public void configureUp() throws FileNotFoundException, NotSupportedException, SystemException, GdibException{
    	// preparo entorno de pruebas
    	testUtils.configureUp();
    	utils.setRootDM(TestUtils.rootDM.getId());
    	utils.setRootCT(TestUtils.rootCT.getId());
    	utils.setRootTemplate(TestUtils.rootTemplate.getId());
    	utils.setRootMigration(TestUtils.rootMigration.getId());
    }

    @After
    public void configureDown() throws IllegalStateException, SecurityException, SystemException{
    	testUtils.configureDown();
    }

    @Test
    public void testTransformNode() throws GdibException{
    	Node nodeExp = repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	NodeRef node = testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321",
    			this.contentType, this.contentDataHandlerFirma, this.contentDataHandlerFirmaMigracion, this.contentDataHandlerZipMigracion);

    	DataNodeTransform nodeTransform = new DataNodeTransform();
    	MigrationID migration = testUtils.createMigrationID("1234567890", "0987654321");
    	MigrationNode migrationNode = migrationServiceSoap.getMigrationNode(migration, true, true, true, null);
    	nodeTransform.setMigrationId(migration);
		List<Property> metadata = new ArrayList<Property>();
		metadata.add(new Property(ConstantUtils.PROP_ID_QNAME, "ES_CAIB_AAAA"));
		metadata.add(new Property(ConstantUtils.PROP_TIPO_ASIENTO_REGISTRAL_QNAME, ConstantUtils.TIPO_ASIENTO_REGISTRAL_1));
		metadata.add(new Property(ConstantUtils.PROP_CODIGO_OFICINA_REGISTRO_QNAME, "codigo_oficina_registro"));
		metadata.add(new Property(ConstantUtils.PROP_TIPO_FIRMA_QNAME, ConstantUtils.TIPO_FIRMA_TF01));
		nodeTransform.setMetadata(metadata);

		String uuidDM = migrationServiceSoap.transformNode(nodeTransform, utils.getProperty(nodeExp.getProperties(), ConstantUtils.PROP_ID_QNAME), null);

		Node transformNode = repositoryServiceSoap.getNode(uuidDM, true, true, null);

		// Checks del Nodo Transformado
		assertEquals("migrated.txt", transformNode.getName());
		assertEquals(ConstantUtils.ENI_MODEL_PREFIX + ConstantUtils.TYPE_DOCUMENTO, transformNode.getType());

		// tiene los aspectos necesarios
		assertNotNull(utils.getAspect(transformNode.getAspects(), ConstantUtils.ASPECT_TRANSFORMADO_QNAME));
		assertNotNull(utils.getAspect(transformNode.getAspects(), ConstantUtils.ASPECT_INTEROPERABLE_QNAME));
		assertNotNull(utils.getAspect(transformNode.getAspects(), ConstantUtils.ASPECT_TRANSFERIBLE_QNAME));
		assertNotNull(utils.getAspect(transformNode.getAspects(), ConstantUtils.ASPECT_FIRMADO_QNAME));

		// metadata old, change, new
		assertEquals(ConstantUtils.CATEGORIA_SERIE, utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_CATEGORIA_QNAME));
		assertEquals(ConstantUtils.TIPO_ASIENTO_REGISTRAL_1, utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_TIPO_ASIENTO_REGISTRAL_QNAME));
		assertEquals("codigo_oficina_registro", utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_CODIGO_OFICINA_REGISTRO_QNAME));
		assertTrue(utils.compareDataHandlers(contentDataHandlerFirmaMigracion, transformNode.getSign()));
		assertTrue(utils.compareDataHandlers(migrationNode.getContent().getData(), transformNode.getContent().getData()));

		// metadata aspect transformado
		assertEquals(ISO8601DateFormat.format(new Date()), utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_FECHA_TRANSFORMACION_QNAME));
		assertEquals(utils.getProperty(migrationNode.getProperties(), ConstantUtils.PROP_FECHA_MIGRACION_QNAME), utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_FECHA_MIGRACION_VALCERT_QNAME));
		assertEquals(utils.getProperty(migrationNode.getProperties(), ConstantUtils.PROP_CODIGO_QNAME), utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_CODIGO_VALCERT_QNAME));
		assertEquals(utils.getProperty(migrationNode.getProperties(), ConstantUtils.PROP_TIPO_DOCUMENTAL_QNAME), utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_TIPO_DOCUMENTAL_VALCERT_QNAME));
		assertEquals(utils.getProperty(migrationNode.getProperties(), ConstantUtils.PROP_CODIGO_EXTERNO_QNAME), utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_CODIGO_EXTERNO_VALCERT_QNAME));
		assertEquals(utils.getProperty(migrationNode.getProperties(), ConstantUtils.PROP_CLASE_QNAME), utils.getProperty(transformNode.getProperties(), ConstantUtils.PROP_CLASE_VALCERT_QNAME));

		// metadatos de la firma de migracion y el zip de migracion del aspecto transformado
		NodeRef transformNodeId = utils.toNodeRef(transformNode.getId());
		assertTrue(utils.compareDataHandlers(contentDataHandlerFirma, utils.getContent(transformNodeId, ConstantUtils.PROP_FIRMAVALCERT_QNAME).getData()));
		assertTrue(utils.compareDataHandlers(contentDataHandlerZipMigracion, utils.getContent(transformNodeId, ConstantUtils.PROP_ZIPMIGRACION_QNAME).getData()));

		// comprobar que el nodo viejo tiene el aspecto transformado y el metadato gdib:migration_uuid es el nuevo nodo creado
		assertTrue(nodeService.hasAspect(utils.toNodeRef(node.getId()), ConstantUtils.ASPECT_TRANSFORMADO_QNAME));
		assertEquals(transformNode.getId(), nodeService.getProperty(utils.toNodeRef(node.getId()), ConstantUtils.PROP_TRANSFORM_UUID_QNAME));
    }

    @Test(expected = GdibException.class)
    public void testTransformNodeMigrationFileNumberNull() throws GdibException{
    	repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321",
    			this.contentType, this.contentDataHandlerFirma, this.contentDataHandlerFirmaMigracion, this.contentDataHandlerZipMigracion);

    	DataNodeTransform nodeTransform = new DataNodeTransform();
    	MigrationID migration = testUtils.createMigrationID("1234567890", "0987654321");
    	nodeTransform.setMigrationId(migration);
		migrationServiceSoap.transformNode(nodeTransform, null, null);
    }

    @Test(expected = GdibException.class)
    public void testTransformNodeMigrationFileNumberEmpty() throws GdibException{
    	repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321",
    			this.contentType, this.contentDataHandlerFirma, this.contentDataHandlerFirmaMigracion, this.contentDataHandlerZipMigracion);

    	DataNodeTransform nodeTransform = new DataNodeTransform();
    	MigrationID migration = testUtils.createMigrationID("1234567890", "0987654321");
    	nodeTransform.setMigrationId(migration);
		migrationServiceSoap.transformNode(nodeTransform, "", null);
    }

    @Test(expected = GdibException.class)
    public void testTransformNodeMigrationFileNumberNotFound() throws GdibException{
    	repositoryServiceSoap.createAndGetNode(nodeExpedient, TestUtils.rootDM.getId(), null);
    	testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321",
    			this.contentType, this.contentDataHandlerFirma, this.contentDataHandlerFirmaMigracion, this.contentDataHandlerZipMigracion);

    	DataNodeTransform nodeTransform = new DataNodeTransform();
    	MigrationID migration = testUtils.createMigrationID("1234567890", "0987654321");
    	nodeTransform.setMigrationId(migration);
		migrationServiceSoap.transformNode(nodeTransform, "aaaaaaaaaaaaaaaa", null);
    }

}

