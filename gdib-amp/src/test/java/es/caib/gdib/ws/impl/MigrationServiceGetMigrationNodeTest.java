package es.caib.gdib.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;

import es.caib.gdib.utils.ConstantUtils;
import es.caib.gdib.utils.GdibUtils;
import es.caib.gdib.utils.TestUtils;
import es.caib.gdib.ws.common.types.Content;
import es.caib.gdib.ws.common.types.MigrationID;
import es.caib.gdib.ws.common.types.MigrationNode;
import es.caib.gdib.ws.exception.GdibException;
import es.caib.gdib.ws.iface.MigrationServiceSoapPort;


@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/context/migrationService-context.xml")
public class MigrationServiceGetMigrationNodeTest {

    static Logger log = Logger.getLogger(MigrationServiceGetMigrationNodeTest.class);

    @Autowired
    private MigrationServiceSoapPort migrationServiceSoap;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private GdibUtils utils;

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

    private MigrationID createMigrationID(String appId, String externalId){
    	MigrationID migration = new MigrationID();
    	migration.setAppId(appId);
        migration.setExternalId(externalId);
    	return migration;
    }

    @Test
    public void testGetMigrationNodeFullContent() throws GdibException{
    	NodeRef node = testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321",
    			this.contentType, this.contentDataHandlerFirma, this.contentDataHandlerFirmaMigracion, this.contentDataHandlerZipMigracion);

    	MigrationID migration = createMigrationID("1234567890", "0987654321");
		MigrationNode mn = migrationServiceSoap.getMigrationNode(migration, true, true, true, null);
		assertEquals(node.getId(), mn.getId());
		assertNotNull(mn.getContent());
		assertNotNull(mn.getSign());
		assertNotNull(mn.getValcertSign());
		assertNotNull(mn.getZipContent());
    }

    @Test
    public void testGetMigrationNodeID() throws GdibException{
    	NodeRef node = testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321");
    	MigrationID migration = createMigrationID("1234567890", "0987654321");
		MigrationNode mn = migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
		assertEquals(node.getId(), mn.getId());
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeTransformed() throws GdibException{
    	NodeRef node = testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321");
    	Map<QName, Serializable> prop = new HashMap<QName, Serializable>();
    	prop.put(ConstantUtils.PROP_TRANSFORM_UUID_QNAME, "uuid transformed");
		utils.addAspect(utils.toNodeRef(node.getId()), ConstantUtils.ASPECT_TRANSFORMADO_QNAME, prop);
		MigrationID migration = createMigrationID("1234567890", "0987654321");
		migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeNoteFound() throws GdibException{
    	MigrationID migration = createMigrationID("12345", "54321");
		migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeIDDuplicate() throws GdibException{
    	testUtils.createMigrationNode("migrated.txt", "1234567890", "0987654321");
    	testUtils.createMigrationNode("migrated2.txt", "1234567890", "0987654321");
    	MigrationID migration = createMigrationID("1234567890", "0987654321");
		migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeMigrationID_Null() throws GdibException{
    	MigrationID migration = createMigrationID(null, null);
		migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeMigrationID_AppIdNull() throws GdibException{
    	MigrationID migration = createMigrationID(null, "0987654321");
		migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeMigrationID_AppIdEmpty() throws GdibException{
    	MigrationID migration = createMigrationID("", "0987654321");
		migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeMigrationID_IdNull() throws GdibException{
    	MigrationID migration = createMigrationID("1234567890", null);
		migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeMigrationID_IdEmpty() throws GdibException{
    	MigrationID migration = createMigrationID("1234567890", "");
		migrationServiceSoap.getMigrationNode(migration , false, false, false, null);
    }

    @Test(expected = GdibException.class)
    public void testGetMigrationNodeMigrationIDNull() throws GdibException{
		migrationServiceSoap.getMigrationNode(null , false, false, false, null);
    }
}

